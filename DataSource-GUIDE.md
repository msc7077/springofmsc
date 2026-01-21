# Master-Slave 데이터소스 설정 가이드

## 📋 목차
1. [Master-Slave란?](#master-slave란)
2. [왜 필요한가?](#왜-필요한가)
3. [동작 방식](#동작-방식)
4. [설정 파일 구조](#설정-파일-구조)
5. [각 클래스의 역할](#각-클래스의-역할)
6. [사용 방법](#사용-방법)
7. [주의사항](#주의사항)

---

## Master-Slave란?

### 기본 개념

**Master-Slave**는 데이터베이스를 두 개로 나누어 사용하는 방식입니다.

- **Master (마스터)**: 쓰기 작업 전용 (INSERT, UPDATE, DELETE)
- **Slave (슬레이브)**: 읽기 작업 전용 (SELECT)

### 왜 두 개로 나누나요?

1. **성능 향상**: 읽기 작업을 여러 DB로 분산
2. **부하 분산**: Master DB의 부하 감소
3. **가용성 향상**: Master에 문제가 있어도 Slave에서 읽기 가능

---

## 왜 필요한가?

### 문제 상황

만약 Master-Slave 설정이 없다면:
- 모든 읽기/쓰기 작업이 하나의 DB에 집중
- 사용자가 많아질수록 DB 부하 증가
- 응답 속도 저하

### 해결 방법

Master-Slave 설정을 하면:
- 읽기 작업은 Slave로 분산 → Master 부하 감소
- 쓰기 작업만 Master로 집중 → 데이터 일관성 유지
- 전체적인 성능 향상

---

## 동작 방식

### 자동 라우팅

Spring Boot가 자동으로 어떤 DB를 사용할지 결정합니다:

```
@Transactional(readOnly = true)  →  Slave DB 사용
@Transactional                    →  Master DB 사용
```

### 동작 흐름

1. **메서드 실행 전**: `DataSourceAspect`가 `@Transactional` 어노테이션 확인
2. **데이터소스 선택**:
   - `readOnly = true` → `DataSourceContextHolder`에 `SLAVE` 설정
   - 그 외 → `DataSourceContextHolder`에 `MASTER` 설정
3. **쿼리 실행**: `RoutingDataSource`가 설정된 타입에 따라 데이터소스 선택
4. **메서드 종료 후**: `DataSourceContextHolder` 정리

---

## 설정 파일 구조

### application.yaml

```yaml
spring:
  datasource:
    # Master-Slave 설정
    master:
      url: jdbc:mysql://localhost:33061/msc_database?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
      username: app_master
      password: 1234
      driver-class-name: com.mysql.cj.jdbc.Driver
    slave:
      url: jdbc:mysql://localhost:33062/msc_database?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
      username: app_slave
      password: 1234
      driver-class-name: com.mysql.cj.jdbc.Driver
```

### 설정 항목 설명

| 항목 | 설명 | 예시 |
|------|------|------|
| `master.url` | Master DB 연결 주소 | `localhost:33061` |
| `master.username` | Master DB 사용자명 | `app_master` |
| `master.password` | Master DB 비밀번호 | `1234` |
| `slave.url` | Slave DB 연결 주소 | `localhost:33062` |
| `slave.username` | Slave DB 사용자명 | `app_slave` |
| `slave.password` | Slave DB 비밀번호 | `1234` |

---

## 각 클래스의 역할

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

**이게 없으면?**
- 모든 요청이 같은 데이터소스를 사용하게 되어 문제 발생

---

### 3. RoutingDataSource.java

**역할**: `DataSourceContextHolder`에 설정된 타입에 따라 Master 또는 Slave 선택

```java
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType type = DataSourceContextHolder.getDataSourceType();
        return type != null ? type : DataSourceType.MASTER; // 기본값은 Master
    }
}
```

**동작 방식**:
1. 쿼리 실행 시점에 `DataSourceContextHolder`에서 타입 조회
2. 타입에 따라 Master 또는 Slave 데이터소스 반환
3. 설정되지 않았으면 기본적으로 Master 사용

---

### 4. DataSourceAspect.java

**역할**: `@Transactional` 어노테이션을 읽어서 자동으로 데이터소스 선택

```java
@Aspect
@Component
public class DataSourceAspect {
    @Around("@annotation(Transactional) || @within(Transactional)")
    public Object determineDataSource(ProceedingJoinPoint joinPoint) {
        // readOnly = true → SLAVE
        // 그 외 → MASTER
    }
}
```

**AOP란?**
- **Aspect-Oriented Programming**: 관점 지향 프로그래밍
- 메서드 실행 전/후에 자동으로 코드를 실행
- 여기서는 `@Transactional` 어노테이션을 감지해서 데이터소스 선택

**이게 없으면?**
- 매번 수동으로 데이터소스를 선택해야 함
- 코드가 복잡해지고 실수하기 쉬움

---

### 5. DataSourceConfig.java

**역할**: Master와 Slave 데이터소스를 생성하고 설정

**주요 구성 요소**:

#### MasterDataSourceProperties / SlaveDataSourceProperties
- `application.yaml`의 설정을 읽어서 Java 객체로 변환
- `@ConfigurationProperties`로 자동 매핑

#### masterDataSource() / slaveDataSource()
- 실제 데이터소스 객체 생성
- HikariCP 연결 풀 사용

#### routingDataSource()
- Master와 Slave를 하나로 묶어서 관리
- `RoutingDataSource`에 등록

#### dataSource() (Primary)
- 실제로 사용할 데이터소스
- `LazyConnectionDataSourceProxy`로 감싸서 실제 쿼리 실행 시점에 선택

**왜 LazyConnectionDataSourceProxy를 사용하나요?**
- 트랜잭션이 시작될 때가 아니라 실제 쿼리 실행 시점에 데이터소스 선택
- 더 정확한 라우팅 가능

---

## 사용 방법

### 읽기 작업 (Slave 사용)

```java
@Service
@Transactional(readOnly = true)  // 클래스 전체에 적용
public class UserService {
    
    public List<User> getAllUsers() {
        return userRepository.findAll();  // → Slave DB 사용
    }
}
```

또는

```java
@Service
public class UserService {
    
    @Transactional(readOnly = true)  // 메서드에만 적용
    public List<User> getAllUsers() {
        return userRepository.findAll();  // → Slave DB 사용
    }
}
```

### 쓰기 작업 (Master 사용)

```java
@Service
public class UserService {
    
    @Transactional  // readOnly 없음
    public User createUser(UserRequestDTO dto) {
        return userRepository.save(user);  // → Master DB 사용
    }
}
```

### 어노테이션이 없으면?

```java
@Service
public class UserService {
    
    public List<User> getAllUsers() {
        return userRepository.findAll();  // → Master DB 사용 (기본값)
    }
}
```

**주의**: 어노테이션이 없으면 기본적으로 Master를 사용합니다.

---

## 주의사항

### 1. Master와 Slave의 데이터 동기화

- Master에 데이터를 쓰면 Slave로 자동 복제되어야 함
- 복제 지연이 있을 수 있으므로, 최신 데이터가 필요하면 Master 사용

### 2. 트랜잭션 범위

```java
@Transactional(readOnly = true)
public void someMethod() {
    List<User> users = userRepository.findAll();  // Slave
    userRepository.save(newUser);  // ❌ 오류! 읽기 전용 트랜잭션에서는 쓰기 불가
}
```

**해결 방법**:
```java
@Transactional(readOnly = true)
public List<User> getAllUsers() {
    return userRepository.findAll();  // Slave
}

@Transactional  // 별도 메서드로 분리
public User createUser(UserRequestDTO dto) {
    return userRepository.save(user);  // Master
}
```

### 3. 스키마 일관성

- Master와 Slave의 테이블 구조가 동일해야 함
- 현재 설정에서는 `ddl-auto: none`으로 스키마 검증 비활성화

### 4. 연결 정보 보안

- `application.yaml`에 비밀번호가 평문으로 저장됨
- 운영 환경에서는 환경 변수나 암호화된 설정 사용 권장

---

## 파일 구조

```
src/main/java/com/example/springofmsc/config/
├── DataSourceType.java              # Master/Slave 구분 enum
├── DataSourceContextHolder.java      # ThreadLocal로 데이터소스 타입 저장
├── RoutingDataSource.java           # 동적 데이터소스 선택
├── DataSourceAspect.java            # AOP로 자동 라우팅
└── DataSourceConfig.java             # 데이터소스 빈 설정

src/main/resources/
└── application.yaml                  # Master/Slave 연결 정보
```

---

## 요약

### 핵심 개념

1. **Master**: 쓰기 작업 (INSERT, UPDATE, DELETE)
2. **Slave**: 읽기 작업 (SELECT)
3. **자동 라우팅**: `@Transactional(readOnly = true)`로 자동 선택

### 사용 규칙

- 읽기 작업 → `@Transactional(readOnly = true)` 추가
- 쓰기 작업 → `@Transactional`만 사용 (또는 생략, 기본값은 Master)

### 이점

- ✅ 성능 향상 (읽기 작업 분산)
- ✅ 부하 분산 (Master 부하 감소)
- ✅ 코드 간결 (자동 라우팅)

---

## 참고사항

- 현재 설정은 **프로젝트 초기 단계**에 맞춘 단순한 구조입니다
- 더 복잡한 요구사항이 생기면 추가 설정이 필요할 수 있습니다
- 실제 운영 환경에서는 모니터링과 장애 대응 방안도 고려해야 합니다
