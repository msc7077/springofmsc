# Master-Slave 데이터베이스 설정 가이드

## 목차
1. [Master-Slave 구조란?](#master-slave-구조란)
2. [필요한 의존성](#필요한-의존성)
3. [설정 파일](#설정-파일)
4. [구현 클래스 설명](#구현-클래스-설명)
5. [동작 원리](#동작-원리)
6. [사용 방법](#사용-방법)

---

## Master-Slave 구조란?

### 개념
- **Master DB**: 읽기와 쓰기 모두 처리하는 메인 데이터베이스
- **Slave DB**: 읽기만 처리하는 복제 데이터베이스

### 왜 사용하나요?
- **성능 향상**: 읽기 요청을 여러 DB로 분산시켜 부하를 줄임
- **가용성 향상**: Master에 문제가 생겨도 Slave에서 읽기 가능

### 이 프로젝트에서의 설정
- **Master**: `127.0.0.1:33061` (app_master/1234)
- **Slave**: `127.0.0.1:33062` (app_slave/1234)

---

## 필요한 의존성

### build.gradle

```gradle
dependencies {
    // 웹 애플리케이션
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // JPA (데이터베이스 연동)
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    
    // AOP (읽기/쓰기 자동 구분)
    implementation 'org.springframework.boot:spring-boot-starter-aop'
    
    // MySQL 드라이버
    runtimeOnly 'com.mysql:mysql-connector-j'
    
    // 테스트
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

### 각 의존성 설명

#### 1. spring-boot-starter-data-jpa
- **역할**: JPA를 사용한 데이터베이스 접근
- **포함 내용**: Hibernate, Spring Data JPA, 트랜잭션 관리
- **이게 없으면**: 데이터베이스에 연결할 수 없음

#### 2. spring-boot-starter-aop
- **역할**: AOP (관점 지향 프로그래밍) 기능 제공
- **이 프로젝트에서의 용도**: `@Transactional` 어노테이션을 보고 읽기/쓰기 자동 구분
- **이게 없으면**: Master-Slave 자동 라우팅이 작동하지 않음

#### 3. mysql-connector-j
- **역할**: MySQL 데이터베이스와 연결하는 JDBC 드라이버
- **이게 없으면**: MySQL에 연결할 수 없음

---

## 설정 파일

### application.yaml

```yaml
spring:
  application:
    name: springofmsc
  
  datasource:
    # Master-Slave 설정
    master:
      url: jdbc:mysql://127.0.0.1:33061/msc_database?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
      username: app_master
      password: 1234
      driver-class-name: com.mysql.cj.jdbc.Driver
    slave:
      url: jdbc:mysql://127.0.0.1:33062/msc_database?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
      username: app_slave
      password: 1234
      driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: none  # 테이블 자동 생성 비활성화
    show-sql: true    # SQL 쿼리 로그 출력
    properties:
      hibernate:
        format_sql: true  # SQL 포맷팅
        dialect: org.hibernate.dialect.MySQLDialect
```

### 설정 항목 설명

| 항목 | 설명 | 예시 |
|------|------|------|
| `master.url` | Master DB 연결 주소 | `127.0.0.1:33061` |
| `master.username` | Master DB 사용자명 | `app_master` |
| `master.password` | Master DB 비밀번호 | `1234` |
| `slave.url` | Slave DB 연결 주소 | `127.0.0.1:33062` |
| `slave.username` | Slave DB 사용자명 | `app_slave` |
| `slave.password` | Slave DB 비밀번호 | `1234` |
| `jpa.hibernate.ddl-auto` | 테이블 자동 생성 설정 | `none` (비활성화) |
| `jpa.show-sql` | SQL 쿼리 로그 출력 여부 | `true` |

---

## 구현 클래스 설명

### 1. DataSourceType.java

**역할**: Master와 Slave를 구분하는 enum

```java
public enum DataSourceType {
    MASTER,  // 쓰기용
    SLAVE    // 읽기용
}
```

**왜 필요한가?**
- 어떤 데이터소스를 사용할지 명확하게 구분하기 위해
- 코드에서 `MASTER` 또는 `SLAVE`로 타입을 지정할 수 있음

**이게 없으면?**
- Master와 Slave를 구분할 방법이 없음

---

### 2. DataSourceContextHolder.java

**역할**: 현재 스레드에서 사용할 데이터소스 타입을 저장

```java
public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();
    
    public static void setDataSourceType(DataSourceType type) { ... }
    public static DataSourceType getDataSourceType() { ... }
    public static void clearDataSourceType() { ... }
}
```

**왜 ThreadLocal을 사용하나요?**
- 각 스레드마다 독립적으로 데이터소스 타입을 저장
- 여러 요청이 동시에 들어와도 서로 영향을 주지 않음
- 예: 요청 A는 Master 사용, 요청 B는 Slave 사용 → 서로 충돌 없음

**이게 없으면?**
- 모든 요청이 같은 데이터소스를 사용하게 되어 문제 발생
- 여러 사용자가 동시에 접속할 때 데이터소스 선택이 꼬임

**메모리 누수 방지**
- `clearDataSourceType()`: 작업 완료 후 ThreadLocal 값을 제거
- 이게 없으면 메모리 누수 발생 가능

---

### 3. RoutingDataSource.java

**역할**: `DataSourceContextHolder`에 설정된 타입에 따라 Master 또는 Slave 선택

```java
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();
        return dataSourceType == DataSourceType.MASTER ? "master" : "slave";
    }
}
```

**동작 방식**
1. `DataSourceContextHolder`에서 현재 타입 조회
2. `MASTER`면 "master" 반환 → Master DB 사용
3. `SLAVE`면 "slave" 반환 → Slave DB 사용

**이게 없으면?**
- Master와 Slave 중 어떤 것을 사용할지 결정할 수 없음
- Spring이 자동으로 데이터소스를 선택할 수 없음

---

### 4. DataSourceConfig.java

**역할**: Master와 Slave 데이터소스를 생성하고 연결

**주요 메서드**

#### masterDataSource()
- Master 데이터소스 생성
- `application.yaml`의 `spring.datasource.master` 설정 읽기
- `Environment`를 사용해서 설정값 주입

#### slaveDataSource()
- Slave 데이터소스 생성
- `application.yaml`의 `spring.datasource.slave` 설정 읽기

#### routingDataSource()
- Master와 Slave를 맵에 등록
- `RoutingDataSource`에 연결 정보 전달
- `@DependsOn`: Master와 Slave가 먼저 생성되도록 보장

#### dataSource()
- 실제로 사용할 데이터소스
- `LazyConnectionDataSourceProxy`로 감싸서 실제 쿼리 실행 시점에 연결 결정
- `@Primary`: 여러 DataSource가 있을 때 이걸 기본으로 사용

**왜 Environment를 사용하나요?**
- `@ConfigurationProperties`를 Bean 메서드에 직접 사용하면 Spring Boot 3.x에서 바인딩이 실패할 수 있음
- `Environment`를 사용하면 확실하게 설정값을 읽을 수 있음

---

### 5. DataSourceAspect.java

**역할**: AOP를 사용해서 읽기/쓰기 작업을 자동으로 구분

```java
@Aspect
@Component
@Order(0)
public class DataSourceAspect {
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object determineDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        // @Transactional(readOnly = true) → Slave
        // @Transactional(readOnly = false) 또는 없음 → Master
    }
}
```

**동작 방식**
1. `@Transactional`이 붙은 메서드를 가로챔 (AOP)
2. `readOnly = true`인지 확인
3. `true`면 → `DataSourceContextHolder`에 `SLAVE` 설정
4. `false` 또는 없으면 → `DataSourceContextHolder`에 `MASTER` 설정
5. 메서드 실행
6. `finally` 블록에서 `clearDataSourceType()` 호출 (메모리 누수 방지)

**왜 AOP를 사용하나요?**
- Service 메서드에 `@Transactional`만 붙이면 자동으로 Master/Slave 선택
- 매번 수동으로 설정할 필요 없음
- 코드가 깔끔해짐

**이게 없으면?**
- Service 메서드에서 매번 수동으로 Master/Slave를 선택해야 함
- 코드가 복잡해지고 실수하기 쉬움

**@Order(0)의 의미**
- 다른 AOP보다 먼저 실행되도록 설정
- 데이터소스 선택이 다른 작업보다 먼저 이루어져야 함

---

## 동작 원리

### 전체 흐름

```
1. Service 메서드 호출
   ↓
2. DataSourceAspect가 @Transactional 확인
   ↓
3. readOnly = true → DataSourceContextHolder에 SLAVE 설정
   readOnly = false → DataSourceContextHolder에 MASTER 설정
   ↓
4. Repository 메서드 실행 (JPA)
   ↓
5. RoutingDataSource가 DataSourceContextHolder 확인
   ↓
6. MASTER면 Master DB 연결, SLAVE면 Slave DB 연결
   ↓
7. 쿼리 실행
   ↓
8. DataSourceAspect의 finally 블록에서 clearDataSourceType() 호출
```

### 예시 시나리오

#### 시나리오 1: 읽기 작업
```java
@Service
public class UserService {
    @Transactional(readOnly = true)  // ← AOP가 이걸 보고
    public List<User> findAll() {
        return userRepository.findAll();  // ← Slave DB로 연결됨
    }
}
```

#### 시나리오 2: 쓰기 작업
```java
@Service
public class UserService {
    @Transactional  // ← readOnly 없음 = 쓰기 작업
    public User save(User user) {
        return userRepository.save(user);  // ← Master DB로 연결됨
    }
}
```

---

## 사용 방법

### 1. Service 클래스 작성

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 읽기 작업 (Slave 사용)
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    // 쓰기 작업 (Master 사용)
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
    
    // 수정 작업 (Master 사용)
    @Transactional
    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow();
        existingUser.setName(user.getName());
        return userRepository.save(existingUser);
    }
}
```

### 2. 주의사항

#### ✅ 올바른 사용
```java
@Transactional(readOnly = true)  // 읽기 → Slave
public List<User> findAll() { ... }

@Transactional  // 쓰기 → Master
public User save(User user) { ... }
```

#### ❌ 잘못된 사용
```java
// readOnly = true인데 쓰기 작업을 하면 안 됨
@Transactional(readOnly = true)
public User save(User user) {  // ← 에러 발생 가능
    return userRepository.save(user);
}
```

### 3. 트랜잭션 없이 사용하면?

```java
// @Transactional이 없으면?
public List<User> findAll() {
    return userRepository.findAll();  // ← 기본값은 MASTER 사용
}
```

- `@Transactional`이 없으면 `DataSourceAspect`가 작동하지 않음
- 기본값인 `MASTER`를 사용하게 됨
- **권장**: 읽기 작업에도 `@Transactional(readOnly = true)` 사용

---

## 문제 해결

### 오류: "jdbcUrl is required with driverClassName"

**원인**: `@ConfigurationProperties`가 Bean 메서드에서 제대로 바인딩되지 않음

**해결**: `Environment`를 사용해서 설정값을 직접 주입

```java
// ❌ 이렇게 하면 안 됨
@Bean
@ConfigurationProperties(prefix = "spring.datasource.master")
public DataSource masterDataSource() {
    return new HikariDataSource();
}

// ✅ 이렇게 해야 함
@Bean
public DataSource masterDataSource() {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(environment.getProperty("spring.datasource.master.url"));
    dataSource.setUsername(environment.getProperty("spring.datasource.master.username"));
    dataSource.setPassword(environment.getProperty("spring.datasource.master.password"));
    dataSource.setDriverClassName(environment.getProperty("spring.datasource.master.driver-class-name"));
    return dataSource;
}
```

---

## 요약

### 핵심 포인트

1. **Master-Slave 구조**: 읽기는 Slave, 쓰기는 Master
2. **자동 라우팅**: `@Transactional(readOnly = true)`로 자동 구분
3. **ThreadLocal**: 각 요청마다 독립적으로 데이터소스 선택
4. **AOP**: 코드를 깔끔하게 유지하면서 자동으로 처리

### 파일 구조

```
src/main/java/com/example/springofmsc/config/
├── DataSourceType.java          # Master/Slave 구분 enum
├── DataSourceContextHolder.java # 현재 스레드의 데이터소스 타입 저장
├── RoutingDataSource.java      # Master/Slave 자동 선택
├── DataSourceConfig.java        # 데이터소스 설정
└── DataSourceAspect.java        # AOP로 읽기/쓰기 자동 구분
```

### 사용 규칙

- **읽기 작업**: `@Transactional(readOnly = true)` 사용
- **쓰기 작업**: `@Transactional` 사용 (readOnly 없음)
- **항상 `@Transactional` 사용 권장**: 명확하게 데이터소스 선택

---

## 참고

- 이 설정은 Spring Boot 3.3.5 기준으로 작성됨
- MySQL 8.0 이상 권장
- Master와 Slave DB가 모두 실행 중이어야 함
- 데이터베이스 `msc_database`가 Master와 Slave에 모두 존재해야 함
