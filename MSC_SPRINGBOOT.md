# Spring Boot 프로젝트 완전 정리 가이드

## 📋 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [프로젝트 구조](#프로젝트-구조)
3. [의존성 (Dependencies) 설명](#의존성-dependencies-설명)
4. [설정 파일 (application.yaml)](#설정-파일-applicationyaml)
5. [Entity (엔티티) - User.java](#entity-엔티티---userjava)
6. [DTO (Data Transfer Object)](#dto-data-transfer-object)
7. [Repository (리포지토리) - UserRepository.java](#repository-리포지토리---userrepositoryjava)
8. [Service (서비스 계층)](#service-서비스-계층)
9. [Controller (컨트롤러)](#controller-컨트롤러)
10. [페이징 처리 (Paging)](#페이징-처리-paging)
11. [Swagger 설정](#swagger-설정)
12. [데이터베이스 설정](#데이터베이스-설정)
13. [파일 업로드/다운로드](#파일-업로드다운로드)
14. [API 엔드포인트 정리](#api-엔드포인트-정리)

---

## 프로젝트 개요

이 프로젝트는 Spring Boot를 사용한 RESTful API 서버입니다. 사용자(User) 정보를 관리하고, 파일을 업로드/다운로드할 수 있는 기능을 제공합니다.

**주요 기능:**
- 사용자 CRUD (생성, 조회, 수정, 삭제)
- 다양한 조건으로 사용자 검색
- 페이징 처리 (Paging)
- 파일 업로드/다운로드
- Swagger를 통한 API 문서화

---

## 프로젝트 구조

```
springofmsc/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/springofmsc/
│   │   │       ├── config/          # 설정 클래스
│   │   │       │   └── SwaggerConfig.java
│   │   │       ├── controller/      # REST API 컨트롤러 (HTTP 요청/응답 처리)
│   │   │       │   ├── UserController.java
│   │   │       │   └── FileController.java
│   │   │       ├── service/          # 비즈니스 로직 계층
│   │   │       │   ├── UserService.java
│   │   │       │   └── FileService.java
│   │   │       ├── repository/      # 데이터 접근 계층
│   │   │       │   └── UserRepository.java
│   │   │       ├── entity/          # 데이터베이스 엔티티
│   │   │       │   └── User.java
│   │   │       ├── dto/              # 데이터 전송 객체
│   │   │       │   ├── UserRequestDTO.java
│   │   │       │   └── UserResponseDTO.java
│   │   │       └── SpringofmscApplication.java  # 메인 클래스
│   │   └── resources/
│   │       └── application.yaml     # 설정 파일
│   └── test/                        # 테스트 코드
├── build.gradle                     # Gradle 빌드 설정
└── uploads/                         # 업로드된 파일 저장 디렉토리
```

---

## 의존성 (Dependencies) 설명

### build.gradle

```gradle
dependencies {
    // Swagger/OpenAPI - API 문서화 도구
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
    
    // Spring Web - REST API 개발을 위한 기본 라이브러리
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Spring Data JPA - 데이터베이스 접근을 위한 ORM 프레임워크
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    
    // MySQL 커넥터 - MySQL 데이터베이스 연결
    runtimeOnly 'com.mysql:mysql-connector-j'
    
    // Lombok - 보일러플레이트 코드 제거 (getter, setter 자동 생성)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // 테스트용 H2 데이터베이스 (인메모리 DB)
    testRuntimeOnly 'com.h2database:h2'
}
```

### 각 의존성 상세 설명

#### 1. springdoc-openapi-starter-webmvc-ui
- **역할**: Swagger UI를 제공하는 라이브러리
- **기능**: 
  - API 문서 자동 생성
  - Swagger UI를 통한 API 테스트
  - OpenAPI 3.0 스펙 지원
- **사용 위치**: `http://localhost:8080/swagger-ui.html`

#### 2. spring-boot-starter-web
- **역할**: Spring MVC를 포함한 웹 애플리케이션 개발 스타터
- **포함 내용**:
  - Spring MVC (REST API 개발)
  - 내장 Tomcat 서버
  - JSON 처리 (Jackson)
  - HTTP 메시지 컨버터

#### 3. spring-boot-starter-data-jpa
- **역할**: JPA (Java Persistence API)를 사용한 데이터베이스 접근
- **포함 내용**:
  - Hibernate (JPA 구현체)
  - Spring Data JPA
  - 트랜잭션 관리
- **장점**: SQL을 직접 작성하지 않고도 데이터베이스 작업 가능

#### 4. mysql-connector-j
- **역할**: MySQL 데이터베이스와 연결하는 JDBC 드라이버
- **기능**: Java 애플리케이션과 MySQL 서버 간 통신

#### 5. lombok
- **역할**: 반복적인 코드를 자동 생성
- **제공 기능**:
  - `@Getter`, `@Setter`: getter/setter 메서드 자동 생성
  - `@NoArgsConstructor`: 기본 생성자 자동 생성
  - `@AllArgsConstructor`: 모든 필드를 파라미터로 받는 생성자 자동 생성
- **장점**: 코드가 간결해지고 가독성 향상

---

## 설정 파일 (application.yaml)

### 전체 설정 내용

```yaml
spring:
  application:
    name: springofmsc  # 애플리케이션 이름
  
  datasource:
    url: jdbc:mysql://localhost:3306/msc_db?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: msc_db_user
    password: 1234
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate  # 테이블 자동 생성 비활성화
    show-sql: true        # SQL 쿼리 로그 출력
    properties:
      hibernate:
        format_sql: true  # SQL 포맷팅
        dialect: org.hibernate.dialect.MySQLDialect
  
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 10MB

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

file:
  upload-dir: uploads
  max-size: 10MB
```

### 설정 항목 상세 설명

#### 1. datasource (데이터 소스)
- **url**: MySQL 데이터베이스 연결 URL
  - `localhost:3306`: MySQL 서버 주소와 포트
  - `msc_db`: 데이터베이스 이름
  - `useSSL=false`: SSL 사용 안 함
  - `serverTimezone=Asia/Seoul`: 서버 시간대 설정
  - `characterEncoding=UTF-8`: 문자 인코딩 설정
- **username/password**: 데이터베이스 접속 정보
- **driver-class-name**: MySQL JDBC 드라이버 클래스

#### 2. jpa (JPA 설정)
- **ddl-auto**: 
  - `validate`: 기존 테이블 구조를 검증만 함 (자동 생성 안 함)
  - `update`: 테이블 자동 생성/수정 (개발 환경용)
  - `create`: 시작 시 테이블 삭제 후 재생성
  - `create-drop`: 종료 시 테이블 삭제
  - `none`: 아무 작업도 안 함
- **show-sql**: SQL 쿼리를 콘솔에 출력 (디버깅용)
- **format_sql**: SQL 쿼리를 보기 좋게 포맷팅
- **dialect**: 사용하는 데이터베이스 방언 (MySQL)

#### 3. servlet.multipart (파일 업로드 설정)
- **enabled**: 파일 업로드 기능 활성화
- **max-file-size**: 개별 파일 최대 크기
- **max-request-size**: 전체 요청 최대 크기

#### 4. springdoc (Swagger 설정)
- **api-docs.path**: OpenAPI JSON 문서 경로
- **swagger-ui.path**: Swagger UI 접근 경로

---

## Entity (엔티티) - User.java

### 전체 코드

```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    private Integer age;

    @Column(length = 200)
    private String address;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 100)
    private String fileType;
}
```

### 어노테이션 상세 설명

#### @Entity
- **의미**: 이 클래스가 JPA 엔티티임을 나타냄
- **역할**: 데이터베이스 테이블과 매핑되는 클래스
- **필수**: JPA를 사용하려면 반드시 필요

#### @Table(name = "users")
- **의미**: 데이터베이스 테이블 이름 지정
- **역할**: 클래스명(User)과 테이블명(users)이 다를 때 사용
- **없으면**: 클래스명을 소문자로 변환한 이름 사용

#### @Id
- **의미**: Primary Key (기본키) 지정
- **역할**: 데이터베이스에서 각 행을 고유하게 식별하는 필드
- **필수**: 엔티티에는 반드시 하나의 @Id가 필요

#### @GeneratedValue(strategy = GenerationType.IDENTITY)
- **의미**: 자동 증가 값 생성 전략
- **strategy 옵션**:
  - `IDENTITY`: 데이터베이스의 AUTO_INCREMENT 사용 (MySQL)
  - `SEQUENCE`: 시퀀스 사용 (Oracle, PostgreSQL)
  - `TABLE`: 별도 테이블 사용
  - `AUTO`: 데이터베이스에 따라 자동 선택
- **동작**: 새 레코드 저장 시 자동으로 값이 증가

#### @Column
- **의미**: 데이터베이스 컬럼과 매핑
- **주요 속성**:
  - `name`: 컬럼 이름 (필드명과 다를 때)
  - `nullable`: NULL 허용 여부 (기본값: true)
  - `unique`: 유일값 제약조건
  - `length`: 문자열 길이 제한

#### Lombok 어노테이션
- **@Getter**: 모든 필드의 getter 메서드 자동 생성
- **@Setter**: 모든 필드의 setter 메서드 자동 생성
- **@NoArgsConstructor**: 파라미터 없는 기본 생성자 생성
- **@AllArgsConstructor**: 모든 필드를 파라미터로 받는 생성자 생성

### 필드 설명

| 필드명 | 타입 | 설명 | 제약조건 |
|--------|------|------|----------|
| id | Long | 자동 증가 ID | Primary Key, 자동 생성 |
| userId | String | 사용자 고유 ID | NOT NULL, UNIQUE, 길이 50 |
| name | String | 이름 | NOT NULL, 길이 50 |
| email | String | 이메일 | NOT NULL, UNIQUE, 길이 100 |
| phone | String | 전화번호 | 길이 20 |
| age | Integer | 나이 | - |
| address | String | 주소 | 길이 200 |
| fileName | String | 원본 파일명 | 길이 255 |
| filePath | String | 파일 저장 경로 | 길이 500 |
| fileSize | Long | 파일 크기 (bytes) | - |
| fileType | String | 파일 타입 (MIME) | 길이 100 |

---

## DTO (Data Transfer Object)

### DTO란?

**DTO (Data Transfer Object)** = 데이터 전송 객체

- **목적**: 계층 간 데이터를 전송하기 위한 객체
- **특징**: 비즈니스 로직이 없음, 필요한 정보만 포함
- **사용 위치**: Controller ↔ Service ↔ Repository 간 데이터 전송

### Entity vs DTO

| 구분 | Entity | DTO |
|------|--------|-----|
| **목적** | 데이터베이스와 매핑 | 데이터 전송 |
| **위치** | `entity` 패키지 | `dto` 패키지 |
| **어노테이션** | `@Entity`, `@Table`, `@Column` | 일반 클래스 (어노테이션 없음) |
| **비즈니스 로직** | 포함 가능 | 포함하지 않음 |
| **필드** | DB 컬럼과 1:1 매핑 | 필요한 필드만 |
| **사용 위치** | Repository, Service | Controller, Service |

### DTO를 사용하는 이유

1. **보안**: 민감한 정보 제거 (filePath, 내부 경로 등)
2. **API 형식 제어**: 클라이언트 요구사항에 맞춤
3. **불필요한 정보 제거**: 클라이언트가 필요 없는 필드 제외
4. **계층 분리**: 각 계층의 책임 분리

### UserRequestDTO.java

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private String address;
    // id는 제외 (자동 생성되므로)
    // 파일 관련 필드는 제외 (별도 API로 처리)
}
```

**사용 목적:**
- 클라이언트로부터 받는 데이터 (POST, PUT 요청)
- Entity의 모든 필드가 필요하지 않음
- 자동 생성되는 필드(id) 제외

### UserResponseDTO.java

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String userId;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private String address;
    private String fileName;      // 원본 파일명만 제공
    private Long fileSize;        // 파일 크기
    private String fileType;       // 파일 타입
    // filePath는 제외 (서버 내부 경로이므로 클라이언트에게 불필요)
}
```

**사용 목적:**
- 클라이언트에게 반환하는 데이터 (GET 요청)
- 불필요한 정보 제거 (filePath 등)
- 보안 강화

### DTO 사용 예시

#### 요청 (Request)
```json
POST /api/users
{
  "userId": "USER001",
  "name": "홍길동",
  "email": "hong@example.com",
  "phone": "010-1234-5678",
  "age": 30,
  "address": "서울시 강남구"
}
```
→ `UserRequestDTO`로 받음

#### 응답 (Response)
```json
{
  "id": 1,
  "userId": "USER001",
  "name": "홍길동",
  "email": "hong@example.com",
  "phone": "010-1234-5678",
  "age": 30,
  "address": "서울시 강남구",
  "fileName": "document.pdf",
  "fileSize": 1024,
  "fileType": "application/pdf"
  // filePath는 제외 ✅
}
```
→ `UserResponseDTO`로 반환

---

## Repository (리포지토리) - UserRepository.java

### 전체 코드

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    List<User> findByName(String name);
    List<User> findByNameContaining(String name);
    List<User> findByNameStartingWith(String name);
    List<User> findByNameEndingWith(String name);
    List<User> findByAge(Integer age);
    List<User> findByNameAndAge(String name, Integer age);
    
    // 복합 조건 검색 (이름, 나이, 휴대번호, 주소) - 페이징 지원
    @Query(value = "SELECT * FROM users WHERE " +
            "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
            "(:age IS NULL OR age = :age) AND " +
            "(:phone IS NULL OR phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:address IS NULL OR address LIKE CONCAT('%', :address, '%'))",
            countQuery = "SELECT COUNT(*) FROM users WHERE " +
            "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
            "(:age IS NULL OR age = :age) AND " +
            "(:phone IS NULL OR phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:address IS NULL OR address LIKE CONCAT('%', :address, '%'))",
            nativeQuery = true)
    Page<User> searchUsers(
            @Param("name") String name,
            @Param("age") Integer age,
            @Param("phone") String phone,
            @Param("address") String address,
            Pageable pageable);

    // 페이징 처리를 위한 전체 사용자 조회
    Page<User> findAll(Pageable pageable);

    // 이름으로 페이징 조회
    Page<User> findByNameContaining(String name, Pageable pageable);

    // 나이로 페이징 조회
    Page<User> findByAge(Integer age, Pageable pageable);
}
```

### 어노테이션 설명

#### @Repository
- **의미**: 이 인터페이스가 데이터 접근 계층임을 나타냄
- **역할**: Spring이 자동으로 빈(Bean)으로 등록
- **장점**: 예외를 Spring의 DataAccessException으로 변환

#### JpaRepository<User, Long>
- **의미**: Spring Data JPA가 제공하는 기본 인터페이스
- **제네릭 타입**:
  - `User`: 엔티티 타입
  - `Long`: Primary Key 타입
- **제공 메서드**:
  - `save(entity)`: 저장/수정
  - `findById(id)`: ID로 조회
  - `findAll()`: 전체 조회
  - `deleteById(id)`: 삭제
  - `existsById(id)`: 존재 여부 확인

### 메서드 이름 규칙 (Query Method)

Spring Data JPA는 메서드 이름을 분석해서 자동으로 쿼리를 생성합니다.

#### 기본 패턴
```
find + By + 필드명 + 조건
```

#### 예시 설명

1. **findByUserId(String userId)**
   - 생성되는 SQL: `SELECT * FROM users WHERE user_id = ?`
   - 반환: `Optional<User>` (0개 또는 1개)

2. **findByName(String name)**
   - 생성되는 SQL: `SELECT * FROM users WHERE name = ?`
   - 반환: `List<User>` (여러 개 가능)

3. **findByNameContaining(String name)**
   - 생성되는 SQL: `SELECT * FROM users WHERE name LIKE %?%`
   - `Containing`: 포함 검색 (LIKE)

4. **findByNameStartingWith(String name)**
   - 생성되는 SQL: `SELECT * FROM users WHERE name LIKE ?%`
   - `StartingWith`: 시작 문자열 검색

5. **findByNameEndingWith(String name)**
   - 생성되는 SQL: `SELECT * FROM users WHERE name LIKE %?`
   - `EndingWith`: 끝 문자열 검색

6. **findByNameAndAge(String name, Integer age)**
   - 생성되는 SQL: `SELECT * FROM users WHERE name = ? AND age = ?`
   - `And`: 여러 조건을 AND로 연결

#### 키워드 정리

| 키워드 | SQL 변환 | 설명 |
|--------|----------|------|
| `findBy` | SELECT | 조회 시작 |
| `And` | AND | 조건 연결 |
| `Or` | OR | 조건 연결 |
| `Containing` | LIKE %?% | 포함 검색 |
| `StartingWith` | LIKE ?% | 시작 검색 |
| `EndingWith` | LIKE %? | 끝 검색 |
| `IsNull` | IS NULL | NULL 체크 |
| `IsNotNull` | IS NOT NULL | NOT NULL 체크 |
| `GreaterThan` | > | 크다 |
| `LessThan` | < | 작다 |
| `Between` | BETWEEN | 범위 검색 |

### @Query 어노테이션

#### 언제 사용하나?
- 메서드 이름으로 표현하기 어려운 복잡한 쿼리
- JOIN이 필요한 경우
- 집계 함수 사용
- 동적 쿼리 (NULL 체크 등)

#### 현재 코드 설명

```java
@Query(value = "SELECT * FROM users WHERE " +
        "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
        "(:age IS NULL OR age = :age) AND " +
        "(:phone IS NULL OR phone LIKE CONCAT('%', :phone, '%')) AND " +
        "(:address IS NULL OR address LIKE CONCAT('%', :address, '%'))", 
        nativeQuery = true)
```

- **nativeQuery = true**: 네이티브 SQL 사용 (MySQL 쿼리 직접 작성)
- **:name, :age 등**: 파라미터 바인딩 (`@Param`으로 연결)
- **동적 쿼리**: 파라미터가 NULL이면 해당 조건 무시

#### @Param
- **의미**: 쿼리의 파라미터와 메서드 파라미터를 연결
- **사용**: `@Query`에서 `:파라미터명`을 사용할 때 필요

### 페이징 메서드

#### Page<T> 반환 타입
- **의미**: 페이징 정보를 포함한 결과 반환
- **제공 정보**: 
  - `content`: 실제 데이터 목록
  - `totalElements`: 전체 항목 수
  - `totalPages`: 전체 페이지 수
  - `number`: 현재 페이지 번호 (0부터 시작)
  - `size`: 페이지 크기
  - `first`, `last`: 첫/마지막 페이지 여부

#### 페이징 메서드 예시

```java
// 전체 사용자 페이징 조회
Page<User> findAll(Pageable pageable);

// 이름으로 페이징 조회
Page<User> findByNameContaining(String name, Pageable pageable);

// 나이로 페이징 조회
Page<User> findByAge(Integer age, Pageable pageable);

// 복합 조건 검색 (페이징 지원)
Page<User> searchUsers(..., Pageable pageable);
```

#### countQuery
- **의미**: 페이징을 위한 전체 개수 조회 쿼리
- **필수**: 네이티브 쿼리에서 페이징 사용 시 반드시 필요
- **역할**: `totalElements`, `totalPages` 계산에 사용

```java
@Query(value = "SELECT * FROM users WHERE ...",
       countQuery = "SELECT COUNT(*) FROM users WHERE ...",
       nativeQuery = true)
Page<User> searchUsers(..., Pageable pageable);
```

#### Optional vs List vs Page 선택 기준

| 반환 타입 | 사용 시나리오 | 예시 |
|----------|-------------|------|
| `Optional<T>` | 고유값으로 검색 (0개 또는 1개) | `findByUserId`, `findByEmail` |
| `List<T>` | 중복 가능한 값으로 검색 (0개 이상) | `findByName`, `findByAge` |
| `Page<T>` | 페이징이 필요한 경우 | `findAll(Pageable)`, `findByNameContaining(..., Pageable)` |

---

## Service (서비스 계층)

### Service 계층이란?

**Service 계층**은 비즈니스 로직을 처리하는 계층입니다.

- **역할**: 비즈니스 로직 처리, 트랜잭션 관리, Entity ↔ DTO 변환
- **위치**: Controller와 Repository 사이
- **책임**: 
  - 비즈니스 규칙 적용
  - 데이터 유효성 검증
  - 트랜잭션 관리
  - Entity와 DTO 변환

### 계층 구조

```
Controller (HTTP 요청/응답)
    ↓ DTO 사용
Service (비즈니스 로직, DTO 변환)
    ↓ Entity 사용
Repository (데이터 접근)
    ↓
Entity (데이터베이스 매핑)
    ↓
Database
```

### UserService.java

#### 클래스 레벨 어노테이션

```java
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

##### @Service
- **의미**: 이 클래스가 Service 계층임을 나타냄
- **역할**: Spring이 자동으로 빈(Bean)으로 등록
- **장점**: 비즈니스 로직을 명확하게 구분

##### @Transactional
- **의미**: 트랜잭션 관리
- **readOnly = true**: 조회 메서드는 읽기 전용 (성능 최적화)
- **사용**: 
  - 조회 메서드: `@Transactional(readOnly = true)` (클래스 레벨)
  - 수정 메서드: `@Transactional` (메서드 레벨, 읽기 전용 오버라이드)

#### 주요 메서드

##### 1. 전체 사용자 조회

```java
public List<UserResponseDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream()
            .map(this::toResponseDTO)
            .toList();
}
```

**동작:**
1. Repository에서 Entity 조회
2. Entity → DTO 변환
3. DTO 리스트 반환

##### 2. 사용자 생성

```java
@Transactional
public UserResponseDTO createUser(UserRequestDTO requestDTO) {
    // 중복 체크
    if (userRepository.findByUserId(requestDTO.getUserId()).isPresent()) {
        throw new IllegalArgumentException("User ID already exists");
    }
    
    // DTO → Entity 변환
    User user = toEntity(requestDTO);
    User savedUser = userRepository.save(user);
    
    // Entity → DTO 변환
    return toResponseDTO(savedUser);
}
```

**동작:**
1. 비즈니스 규칙 검증 (중복 체크)
2. DTO → Entity 변환
3. 저장
4. Entity → DTO 변환하여 반환

##### 3. Entity ↔ DTO 변환 메서드

```java
// Entity → ResponseDTO 변환
private UserResponseDTO toResponseDTO(User user) {
    return new UserResponseDTO(
            user.getId(),
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getAge(),
            user.getAddress(),
            user.getFileName(),
            user.getFileSize(),
            user.getFileType()
    );
}

// RequestDTO → Entity 변환
private User toEntity(UserRequestDTO requestDTO) {
    User user = new User();
    user.setUserId(requestDTO.getUserId());
    user.setName(requestDTO.getName());
    user.setEmail(requestDTO.getEmail());
    user.setPhone(requestDTO.getPhone());
    user.setAge(requestDTO.getAge());
    user.setAddress(requestDTO.getAddress());
    return user;
}
```

**역할:**
- Entity와 DTO 간 변환 로직 캡슐화
- 변환 로직 변경 시 한 곳만 수정

### FileService.java

#### 파일 업로드

```java
@Transactional
public String uploadFile(Long userId, MultipartFile file) throws IOException {
    // 사용자 조회
    User user = userService.getUserEntity(userId);
    
    // 파일 저장 로직...
    // 사용자 정보 업데이트
    return originalFilename;
}
```

#### 파일 다운로드

```java
@Transactional(readOnly = true)
public FileDownloadResult downloadFile(Long userId) throws IOException {
    User user = userService.getUserEntity(userId);
    
    // 파일 리소스 생성
    // 한글 파일명 인코딩
    return new FileDownloadResult(resource, mediaType, contentDisposition);
}
```

### Service 계층의 장점

1. **책임 분리**
   - Controller: HTTP 요청/응답만 처리
   - Service: 비즈니스 로직 처리
   - Repository: 데이터 접근만 처리

2. **재사용성**
   - 여러 Controller에서 같은 Service 메서드 사용 가능
   - 비즈니스 로직 중복 제거

3. **테스트 용이성**
   - Service 계층만 단위 테스트 가능
   - Mock Repository 사용 가능

4. **트랜잭션 관리**
   - `@Transactional`로 자동 트랜잭션 관리
   - 여러 Repository 작업을 하나의 트랜잭션으로 처리

5. **유지보수성**
   - 비즈니스 로직이 한 곳에 집중
   - 변경 시 영향 범위 최소화

---

## Controller (컨트롤러)

### 컨트롤러란?
- **역할**: HTTP 요청을 받아서 처리하고 응답을 반환
- **위치**: 클라이언트와 서버 사이의 중간 계층
- **책임**: 
  - 요청 파라미터 받기
  - Service 계층 호출 (비즈니스 로직은 Service에서 처리)
  - HTTP 응답 반환
- **원칙**: 비즈니스 로직은 포함하지 않음 (Service 계층으로 위임)

### UserController.java

#### 클래스 레벨 어노테이션

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 CRUD API")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
```

##### @RestController
- **의미**: REST API 컨트롤러임을 나타냄
- **포함 기능**: `@Controller` + `@ResponseBody`
- **동작**: 
  - 메서드 반환값을 자동으로 JSON으로 변환
  - `@ResponseBody`를 각 메서드에 붙일 필요 없음

##### @RequestMapping("/api/users")
- **의미**: 이 컨트롤러의 기본 URL 경로 설정
- **역할**: 모든 메서드의 URL 앞에 `/api/users`가 붙음
- **예시**: `@GetMapping("/{id}")` → `/api/users/{id}`

##### @Tag
- **의미**: Swagger UI에서 API 그룹화
- **역할**: Swagger UI에서 "User" 섹션으로 표시

##### 의존성 주입 (Dependency Injection)
```java
private final UserService userService;

public UserController(UserService userService) {
    this.userService = userService;
}
```
- **의미**: Spring이 자동으로 UserService를 주입
- **방식**: 생성자 주입 (Constructor Injection)
- **장점**: 
  - 필수 의존성 보장
  - 테스트 용이
  - 불변성 보장 (final)
- **변경 사항**: Repository 대신 Service를 주입받음

#### HTTP 메서드 어노테이션

##### @GetMapping
- **의미**: HTTP GET 요청 처리
- **용도**: 데이터 조회
- **특징**: 
  - URL에 파라미터 포함 가능
  - 캐싱 가능
  - 멱등성 (같은 요청 = 같은 결과)

##### @PostMapping
- **의미**: HTTP POST 요청 처리
- **용도**: 데이터 생성
- **특징**: 
  - 요청 본문에 데이터 포함
  - 멱등성 없음 (같은 요청도 다른 결과 가능)

##### @PutMapping
- **의미**: HTTP PUT 요청 처리
- **용도**: 데이터 전체 수정
- **특징**: 
  - 멱등성 있음
  - 전체 리소스 교체

##### @DeleteMapping
- **의미**: HTTP DELETE 요청 처리
- **용도**: 데이터 삭제
- **특징**: 
  - 멱등성 있음
  - 삭제 후에도 같은 요청 가능 (이미 삭제됨)

#### 파라미터 어노테이션

##### @PathVariable
- **의미**: URL 경로에서 변수 추출
- **예시**: `/api/users/{id}` → `@PathVariable Long id`
- **사용**: RESTful API에서 리소스 ID 전달

##### @RequestParam
- **의미**: 쿼리 파라미터에서 값 추출
- **예시**: `/api/users/search?name=홍길동&age=30`
- **속성**:
  - `required = false`: 선택적 파라미터
  - `defaultValue`: 기본값 설정

##### @RequestBody
- **의미**: HTTP 요청 본문(JSON)을 객체로 변환
- **예시**: POST 요청의 JSON 데이터를 User 객체로 변환
- **필수**: `Content-Type: application/json`

#### 응답 처리

##### ResponseEntity<T>
- **의미**: HTTP 응답을 세밀하게 제어
- **제공 기능**:
  - 상태 코드 설정
  - 헤더 설정
  - 본문 데이터 설정

##### 사용 예시

```java
// 200 OK + 데이터
return ResponseEntity.ok(users);

// 201 Created + 데이터
return ResponseEntity.status(201).body(savedUser);

// 404 Not Found
return ResponseEntity.notFound().build();

// 204 No Content (본문 없음)
return ResponseEntity.noContent().build();

// 400 Bad Request
return ResponseEntity.badRequest().build();
```

##### Optional 처리

```java
Optional<User> user = userRepository.findById(id);
return user.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
```

- **map()**: 값이 있으면 처리
- **orElse()**: 값이 없으면 대체값 반환
- **의미**: null 체크를 안전하게 처리

### 주요 메서드 상세 설명

#### 1. 전체 사용자 조회

```java
@GetMapping
public ResponseEntity<?> getAllUsers(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort) {
    
    // 페이징 파라미터가 없으면 전체 목록 반환
    if (page == null && size == null && sort == null) {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    // 페이징 처리
    int pageNumber = (page != null && page >= 0) ? page : 0;
    int pageSize = (size != null && size > 0) ? size : 10;
    
    Page<UserResponseDTO> userPage = userService.getAllUsers(pageNumber, pageSize, sort);
    return ResponseEntity.ok(userPage);
}
```

- **URL**: `GET /api/users`
- **동작**: 
  1. Service의 `getAllUsers()` 호출
  2. Service에서 Repository 조회 및 DTO 변환
  3. DTO 리스트 또는 Page 반환
- **변경 사항**: 
  - Repository 직접 호출 → Service 호출
  - Entity 반환 → DTO 반환

#### 2. ID로 조회

```java
@GetMapping("/{id}")
public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
    return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

- **URL**: `GET /api/users/1`
- **동작**:
  1. URL에서 `id` 추출
  2. Service에서 조회 및 DTO 변환
  3. 있으면 200 OK + DTO, 없으면 404 Not Found
- **변경 사항**: 
  - Repository 직접 호출 → Service 호출
  - Entity 반환 → DTO 반환

#### 3. 사용자 생성

```java
@PostMapping
public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO requestDTO) {
    try {
        UserResponseDTO savedUser = userService.createUser(requestDTO);
        return ResponseEntity.status(201).body(savedUser);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
```

- **URL**: `POST /api/users`
- **요청 본문**: JSON 형식의 `UserRequestDTO` 객체
- **동작**:
  1. Service에서 중복 체크 및 비즈니스 로직 처리
  2. DTO → Entity 변환 후 저장
  3. Entity → DTO 변환하여 반환
  4. 201 Created 반환
- **변경 사항**: 
  - Entity 직접 받기 → DTO 받기
  - Controller에서 중복 체크 → Service에서 처리
  - Entity 반환 → DTO 반환

#### 4. 사용자 수정

```java
@PutMapping("/{id}")
public ResponseEntity<UserResponseDTO> updateUser(
        @PathVariable Long id,
        @RequestBody UserRequestDTO requestDTO) {
    try {
        UserResponseDTO updatedUser = userService.updateUser(id, requestDTO);
        return ResponseEntity.ok(updatedUser);
    } catch (IllegalArgumentException e) {
        if (e.getMessage().contains("not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
```

- **URL**: `PUT /api/users/1`
- **요청 본문**: JSON 형식의 `UserRequestDTO` 객체
- **동작**:
  1. Service에서 기존 사용자 조회 및 수정 로직 처리
  2. 중복 체크 및 업데이트
  3. Entity → DTO 변환하여 반환
  4. 200 OK 반환
- **변경 사항**: 
  - Entity 직접 받기 → DTO 받기
  - Controller에서 수정 로직 → Service에서 처리
  - Entity 반환 → DTO 반환

#### 5. 복합 조건 검색

```java
@GetMapping("/search")
public ResponseEntity<?> searchUsers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer age,
        @RequestParam(required = false) String phone,
        @RequestParam(required = false) String address,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort) {
    
    // 페이징 파라미터가 없으면 전체 목록 반환
    if (page == null && size == null && sort == null) {
        List<UserResponseDTO> users = userService.searchUsers(name, age, phone, address);
        return ResponseEntity.ok(users);
    }
    
    // 페이징 처리
    int pageNumber = (page != null && page >= 0) ? page : 0;
    int pageSize = (size != null && size > 0) ? size : 10;
    
    Page<UserResponseDTO> userPage = userService.searchUsers(
            name, age, phone, address, pageNumber, pageSize, sort);
    return ResponseEntity.ok(userPage);
}
```

- **URL**: `GET /api/users/search?name=홍길동&age=30`
- **동작**:
  1. Query Parameter로 조건 받기
  2. Service의 `searchUsers()` 호출
  3. Service에서 Repository 호출 및 DTO 변환
  4. DTO 리스트 또는 Page 반환
- **변경 사항**: 
  - Repository 직접 호출 → Service 호출
  - Entity 반환 → DTO 반환
  - 페이징 지원 추가

---

## 페이징 처리 (Paging)

### 페이징이란?

대량의 데이터를 작은 단위로 나누어 조회하는 기능입니다.

**장점:**
- 성능 향상 (한 번에 모든 데이터를 조회하지 않음)
- 메모리 효율성
- 사용자 경험 개선 (빠른 응답)

### Spring Data JPA 페이징

#### Pageable 인터페이스

```java
Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
```

- **pageNumber**: 페이지 번호 (0부터 시작)
- **pageSize**: 한 페이지에 표시할 항목 수
- **sort**: 정렬 기준

#### Page<T> 인터페이스

```java
Page<User> userPage = userRepository.findAll(pageable);
```

**제공 필드:**
- `content`: 현재 페이지의 데이터 목록
- `totalElements`: 전체 항목 수
- `totalPages`: 전체 페이지 수
- `number`: 현재 페이지 번호 (0부터)
- `size`: 페이지 크기
- `first`: 첫 페이지 여부
- `last`: 마지막 페이지 여부
- `hasNext`: 다음 페이지 존재 여부
- `hasPrevious`: 이전 페이지 존재 여부

### 페이징 사용 예시

#### 1. 전체 사용자 조회 (페이징)

```java
@GetMapping
public ResponseEntity<?> getAllUsers(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort) {
    
    // 페이징 파라미터가 없으면 전체 목록 반환
    if (page == null && size == null && sort == null) {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    // 페이징 처리
    int pageNumber = (page != null && page >= 0) ? page : 0;
    int pageSize = (size != null && size > 0) ? size : 10;
    
    // 정렬 설정
    Sort sortObj = Sort.by(Sort.Direction.ASC, "id");
    if (sort != null && !sort.isEmpty()) {
        String[] sortParams = sort.split(",");
        if (sortParams.length == 2) {
            String field = sortParams[0].trim();
            String direction = sortParams[1].trim().toLowerCase();
            sortObj = Sort.by(
                "desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                field
            );
        }
    }
    
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);
    Page<User> userPage = userRepository.findAll(pageable);
    
    return ResponseEntity.ok(userPage);
}
```

#### 2. 이름 검색 (페이징)

```java
@GetMapping("/name/contains/{keyword}")
public ResponseEntity<?> getUsersByNameContaining(
        @PathVariable String keyword,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort) {
    
    // 페이징 파라미터가 없으면 전체 목록 반환
    if (page == null && size == null && sort == null) {
        List<User> users = userRepository.findByNameContaining(keyword);
        return ResponseEntity.ok(users);
    }
    
    // 페이징 처리
    int pageNumber = (page != null && page >= 0) ? page : 0;
    int pageSize = (size != null && size > 0) ? size : 10;
    
    Sort sortObj = Sort.by(Sort.Direction.ASC, "name");
    if (sort != null && !sort.isEmpty()) {
        // 정렬 설정 로직...
    }
    
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);
    Page<User> userPage = userRepository.findByNameContaining(keyword, pageable);
    
    return ResponseEntity.ok(userPage);
}
```

#### 3. 복합 조건 검색 (페이징)

```java
@GetMapping("/search")
public ResponseEntity<?> searchUsers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer age,
        @RequestParam(required = false) String phone,
        @RequestParam(required = false) String address,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort) {
    
    // 페이징 파라미터 설정
    // 페이징 파라미터가 없으면 전체 목록을 반환하기 위해 큰 페이지 크기 사용
    int pageNumber = (page != null && page >= 0) ? page : 0;
    int pageSize = (size != null && size > 0) ? size : Integer.MAX_VALUE;
    
    // 정렬 설정
    Sort sortObj = Sort.by(Sort.Direction.ASC, "id");
    if (sort != null && !sort.isEmpty()) {
        // 정렬 설정 로직...
    }
    
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);
    Page<User> userPage = userRepository.searchUsers(name, age, phone, address, pageable);
    
    // 페이징 파라미터가 없으면 List로 변환하여 반환
    if (page == null && size == null && sort == null) {
        return ResponseEntity.ok(userPage.getContent());
    }
    
    // 페이징 파라미터가 있으면 Page 객체 반환
    return ResponseEntity.ok(userPage);
}
```

### 페이징 API 사용 방법

#### URL 예시

```
# 페이징 없이 전체 조회
GET /api/users

# 페이징 적용
GET /api/users?page=0&size=10&sort=id,asc

# 두 번째 페이지, 5개씩, 이름 오름차순
GET /api/users?page=1&size=5&sort=name,asc

# 이름 검색 + 페이징
GET /api/users/name/contains/홍?page=0&size=10&sort=id,desc

# 복합 검색 + 페이징
GET /api/users/search?name=홍길동&age=30&page=0&size=10&sort=age,desc
```

#### 정렬 옵션

- `id,asc`: ID 오름차순
- `id,desc`: ID 내림차순
- `name,asc`: 이름 오름차순
- `name,desc`: 이름 내림차순
- `age,asc`: 나이 오름차순
- `age,desc`: 나이 내림차순

### 페이징 응답 형식

#### 페이징 파라미터 없을 때

```json
[
  { "id": 1, "name": "홍길동", ... },
  { "id": 2, "name": "김철수", ... }
]
```

#### 페이징 파라미터 있을 때

```json
{
  "content": [
    { "id": 1, "name": "홍길동", ... },
    { "id": 2, "name": "김철수", ... }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 35,
  "totalPages": 4,
  "last": false,
  "first": true,
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "empty": false
}
```

### Page 객체 필드 설명

| 필드 | 설명 | 계산 방법 |
|------|------|----------|
| `content` | 현재 페이지의 데이터 배열 | `SELECT * FROM ... LIMIT ... OFFSET ...` |
| `totalElements` | 전체 항목 수 | `SELECT COUNT(*) FROM ...` |
| `totalPages` | 전체 페이지 수 | `Math.ceil(totalElements / size)` |
| `number` | 현재 페이지 번호 (0부터) | `pageable.getPageNumber()` |
| `size` | 페이지 크기 | `pageable.getPageSize()` |
| `first` | 첫 페이지 여부 | `number == 0` |
| `last` | 마지막 페이지 여부 | `number >= totalPages - 1` |
| `hasNext` | 다음 페이지 존재 여부 | `!last` |
| `hasPrevious` | 이전 페이지 존재 여부 | `!first` |
| `numberOfElements` | 현재 페이지 항목 수 | `content.size()` |
| `empty` | 빈 페이지 여부 | `content.isEmpty()` |

### Repository 메서드 통합

#### 통합 전 (중복 코드)

```java
// 페이징 없음
List<User> searchUsers(...);

// 페이징 지원
Page<User> searchUsersWithPaging(..., Pageable pageable);
```

**문제점:**
- 쿼리 중복
- 유지보수 어려움

#### 통합 후 (권장)

```java
// 하나의 메서드로 통합
Page<User> searchUsers(..., Pageable pageable);
```

**장점:**
- 쿼리 중복 제거
- 유지보수 용이
- 코드 간결화

**Controller에서 처리:**
```java
// 페이징 파라미터 없을 때: 큰 페이지 크기 사용
int pageSize = (size != null && size > 0) ? size : Integer.MAX_VALUE;
Pageable pageable = PageRequest.of(0, pageSize);
Page<User> userPage = userRepository.searchUsers(..., pageable);

// 페이징 파라미터 없으면 List로 변환
if (page == null && size == null) {
    return ResponseEntity.ok(userPage.getContent());
}
```

---

## Swagger 설정

### SwaggerConfig.java

```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot API")
                        .version("1.0.0")
                        .description("Spring Boot 애플리케이션 API 문서"));
    }
}
```

#### @Configuration
- **의미**: 이 클래스가 Spring 설정 클래스임을 나타냄
- **역할**: 빈(Bean) 정의를 포함하는 클래스

#### @Bean
- **의미**: 메서드가 반환하는 객체를 Spring 빈으로 등록
- **역할**: Spring 컨테이너가 관리하는 객체 생성
- **사용**: 다른 클래스에서 주입받아 사용 가능

#### OpenAPI
- **의미**: OpenAPI 3.0 스펙을 따르는 API 문서 객체
- **역할**: Swagger UI에 표시될 API 정보 설정

### Swagger 어노테이션

#### @Operation
- **의미**: API 엔드포인트에 대한 설명
- **속성**:
  - `summary`: 짧은 설명
  - `description`: 상세 설명

#### @Tag
- **의미**: API를 그룹화
- **역할**: Swagger UI에서 섹션으로 표시

#### @Parameter
- **의미**: API 파라미터 설명
- **속성**:
  - `description`: 파라미터 설명
  - `required`: 필수 여부
  - `example`: 예시 값

---

## 데이터베이스 설정

### 테이블 생성 스크립트

#### init-database.sql

```sql
-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS springofmsc 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE springofmsc;

-- users 테이블 생성
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '자동 증가 ID',
    user_id VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자 고유 ID',
    name VARCHAR(50) NOT NULL COMMENT '이름',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일',
    phone VARCHAR(20) COMMENT '전화번호',
    age INT COMMENT '나이',
    address VARCHAR(200) COMMENT '주소',
    file_name VARCHAR(255) COMMENT '원본 파일명',
    file_path VARCHAR(500) COMMENT '파일 저장 경로',
    file_size BIGINT COMMENT '파일 크기 (bytes)',
    file_type VARCHAR(100) COMMENT '파일 타입 (MIME type)',
    INDEX idx_email (email),
    INDEX idx_name (name),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 운영 환경 컬럼 추가 스크립트

#### add-file-columns.sql

```sql
USE msc_db;

ALTER TABLE users 
ADD COLUMN file_name VARCHAR(255) COMMENT '원본 파일명' AFTER address,
ADD COLUMN file_path VARCHAR(500) COMMENT '파일 저장 경로' AFTER file_name,
ADD COLUMN file_size BIGINT COMMENT '파일 크기 (bytes)' AFTER file_path,
ADD COLUMN file_type VARCHAR(100) COMMENT '파일 타입 (MIME type)' AFTER file_size;
```

### SQL 키워드 설명

- **AUTO_INCREMENT**: 자동 증가 (1, 2, 3...)
- **PRIMARY KEY**: 기본키 (고유 식별자)
- **UNIQUE**: 유일값 제약조건
- **NOT NULL**: NULL 값 허용 안 함
- **INDEX**: 검색 속도 향상을 위한 인덱스
- **ENGINE=InnoDB**: MySQL 스토리지 엔진
- **utf8mb4**: UTF-8 인코딩 (이모지 지원)

---

## 파일 업로드/다운로드

### FileController.java

#### 파일 업로드

**Controller:**
```java
@PostMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<String> uploadFile(
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file) {
    
    try {
        String fileName = fileService.uploadFile(id, file);
        return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다: " + fileName);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IOException e) {
        return ResponseEntity.internalServerError().body("파일 업로드 실패: " + e.getMessage());
    }
}
```

**Service:**
```java
@Transactional
public String uploadFile(Long userId, MultipartFile file) throws IOException {
    // 사용자 조회
    User user = userService.getUserEntity(userId);
    
    // 업로드 디렉토리 생성
    // 고유한 파일명 생성
    // 파일 저장
    // 사용자 정보 업데이트
    
    return originalFilename;
}
```

##### 동작 과정

1. **MultipartFile 받기**
   - `@RequestParam("file")`: form-data에서 "file" 필드 추출
   - `consumes = MediaType.MULTIPART_FORM_DATA_VALUE`: multipart/form-data만 허용

2. **디렉토리 생성**
   - `Paths.get(uploadDir)`: 경로 객체 생성
   - `Files.createDirectories()`: 디렉토리 생성 (없으면)

3. **고유 파일명 생성**
   - `UUID.randomUUID()`: 고유한 ID 생성
   - 원본 파일명의 확장자 추출
   - UUID + 확장자로 저장 (중복 방지)

4. **파일 저장**
   - `Files.copy()`: 파일 복사
   - `StandardCopyOption.REPLACE_EXISTING`: 기존 파일 덮어쓰기

5. **데이터베이스 저장**
   - 원본 파일명, 경로, 크기, 타입 저장

#### 파일 다운로드

**Controller:**
```java
@GetMapping("/{id}/file")
public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
    try {
        FileDownloadResult result = fileService.downloadFile(id);
        return ResponseEntity.ok()
                .contentType(result.getMediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION, result.getContentDisposition())
                .body(result.getResource());
    } catch (IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    } catch (IOException e) {
        return ResponseEntity.internalServerError().build();
    }
}
```

**Service:**
```java
@Transactional(readOnly = true)
public FileDownloadResult downloadFile(Long userId) throws IOException {
    // 사용자 조회
    // 파일 리소스 생성
    // 한글 파일명 인코딩 처리
    // 결과 반환
}
```

##### 동작 과정

1. **사용자 조회**: 파일 정보가 있는지 확인
2. **Resource 생성**: 파일을 Spring Resource로 변환
3. **파일명 인코딩**: 
   - 한글 포함 시: RFC 5987 형식 (`filename*=UTF-8''인코딩된파일명`)
   - ASCII만: 일반 형식 (`filename="파일명"`)
4. **Content-Disposition 헤더**: 브라우저가 다운로드하도록 설정
5. **응답 반환**: 파일 스트림 전송

##### 한글 파일명 처리

- **문제**: HTTP 헤더는 ASCII만 허용
- **해결**: RFC 5987 형식으로 인코딩
- **방법**: `URLEncoder.encode()` 사용

#### 파일 삭제

**Controller:**
```java
@PostMapping("/{id}/file/delete")
public ResponseEntity<String> deleteFile(@PathVariable Long id) {
    try {
        fileService.deleteFile(id);
        return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IOException e) {
        return ResponseEntity.internalServerError().body("파일 삭제 실패: " + e.getMessage());
    }
}
```

**Service:**
```java
@Transactional
public void deleteFile(Long userId) throws IOException {
    // 사용자 조회
    // 파일 삭제
    // 데이터베이스에서 파일 정보 제거
}
```

---

## API 엔드포인트 정리

### 사용자 관리 API

| 메서드 | URL | 설명 | 요청 본문 | 응답 |
|--------|-----|------|----------|------|
| 메서드 | URL | 설명 | 요청 본문 | 응답 |
|--------|-----|------|----------|------|
| GET | `/api/users` | 전체 사용자 조회 | - | UserResponseDTO[] 또는 Page<UserResponseDTO> |
| GET | `/api/users?page=0&size=10&sort=id,asc` | 전체 사용자 조회 (페이징) | Query Params | Page<UserResponseDTO> |
| GET | `/api/users/{id}` | ID로 조회 | - | UserResponseDTO |
| GET | `/api/users/user-id/{userId}` | user_id로 조회 | - | UserResponseDTO |
| GET | `/api/users/email/{email}` | 이메일로 조회 | - | UserResponseDTO |
| GET | `/api/users/name/{name}` | 이름으로 조회 | - | UserResponseDTO[] |
| GET | `/api/users/name/contains/{keyword}` | 이름 부분 일치 | - | UserResponseDTO[] 또는 Page<UserResponseDTO> |
| GET | `/api/users/name/contains/{keyword}?page=0&size=10` | 이름 부분 일치 (페이징) | Query Params | Page<UserResponseDTO> |
| GET | `/api/users/age/{age}` | 나이로 조회 | - | UserResponseDTO[] 또는 Page<UserResponseDTO> |
| GET | `/api/users/age/{age}?page=0&size=10` | 나이로 조회 (페이징) | Query Params | Page<UserResponseDTO> |
| GET | `/api/users/search` | 복합 조건 검색 | Query Params | UserResponseDTO[] 또는 Page<UserResponseDTO> |
| GET | `/api/users/search?name=홍&page=0&size=10` | 복합 조건 검색 (페이징) | Query Params | Page<UserResponseDTO> |
| POST | `/api/users` | 사용자 생성 | UserRequestDTO (JSON) | UserResponseDTO (201) |
| PUT | `/api/users/{id}` | 사용자 수정 | UserRequestDTO (JSON) | UserResponseDTO |
| DELETE | `/api/users/{id}` | 사용자 삭제 | - | 204 No Content |

### 파일 관리 API

| 메서드 | URL | 설명 | 요청 | 응답 |
|--------|-----|------|------|------|
| POST | `/api/users/{id}/file` | 파일 업로드 | multipart/form-data | String |
| GET | `/api/users/{id}/file` | 파일 다운로드 | - | File |
| POST | `/api/users/{id}/file/delete` | 파일 삭제 | - | String |

### 페이징 파라미터

모든 조회 API에서 페이징 파라미터를 제공할 수 있습니다:

- `page`: 페이지 번호 (0부터 시작, 선택사항)
- `size`: 페이지 크기 (한 페이지에 표시할 항목 수, 선택사항)
- `sort`: 정렬 기준 (필드명,asc 또는 필드명,desc, 선택사항)

**예시:**
```
GET /api/users?page=0&size=10&sort=id,asc
GET /api/users/name/contains/홍?page=0&size=5&sort=name,desc
GET /api/users/search?name=홍길동&age=30&page=0&size=10&sort=age,asc
```

### 복합 검색 예시

```
GET /api/users/search?name=홍길동&age=30&phone=7077&address=서울시
```

- 모든 파라미터는 선택사항
- NULL인 조건은 무시됨
- 여러 조건을 AND로 결합
- 페이징 파라미터 추가 가능

---

## 주요 개념 정리

### 1. Spring Boot의 계층 구조

```
Controller (컨트롤러)
    ↓ DTO 사용
Service (서비스 계층) - 비즈니스 로직
    ↓ Entity 사용
Repository (리포지토리) - 데이터 접근
    ↓
Entity (엔티티) - 데이터베이스 매핑
    ↓
Database (데이터베이스)
```

- **Controller**: HTTP 요청/응답 처리, DTO 사용
- **Service**: 비즈니스 로직 처리, 트랜잭션 관리, Entity ↔ DTO 변환
- **Repository**: 데이터베이스 접근, Entity 사용
- **Entity**: 데이터베이스 테이블과 매핑
- **DTO**: 계층 간 데이터 전송

### 2. 의존성 주입 (Dependency Injection)

```java
// 생성자 주입 (권장)
public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

- **의미**: Spring이 자동으로 필요한 객체를 주입
- **장점**: 
  - 코드 간결
  - 테스트 용이
  - 느슨한 결합

### 3. Optional 사용

```java
Optional<User> user = userRepository.findById(id);
return user.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
```

- **의미**: null을 안전하게 처리
- **장점**: NullPointerException 방지

### 4. HTTP 상태 코드

| 코드 | 의미 | 사용 예시 |
|------|------|----------|
| 200 | OK | 조회 성공 |
| 201 | Created | 생성 성공 |
| 204 | No Content | 삭제 성공 (본문 없음) |
| 400 | Bad Request | 잘못된 요청 |
| 404 | Not Found | 리소스 없음 |
| 500 | Internal Server Error | 서버 오류 |

---

## 실행 및 테스트

### 1. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 Cursor에서 `SpringofmscApplication.java` 파일을 열고 Run 버튼 클릭

### 2. Swagger UI 접속

```
http://localhost:8080/swagger-ui.html
```

### 3. API 테스트

Swagger UI에서:
1. "Try it out" 클릭
2. 파라미터 입력
3. "Execute" 클릭
4. 결과 확인

---

## 문제 해결 가이드

### 1. 포트 충돌

**오류**: `Port 8080 was already in use`

**해결**:
```bash
lsof -i :8080  # 포트 사용 프로세스 확인
kill -9 [PID]  # 프로세스 종료
```

또는 `application.yaml`에서 포트 변경:
```yaml
server:
  port: 8081
```

### 2. 데이터베이스 연결 오류

**확인 사항**:
- MySQL 서비스 실행 중인지
- `application.yaml`의 비밀번호가 맞는지
- 데이터베이스가 생성되어 있는지

### 3. 한글 파일명 오류

**해결**: `FileController`의 인코딩 로직 사용 (이미 구현됨)

---

## 다음 단계 학습 추천

1. **유효성 검증 (Validation)**
   - `@NotNull`, `@Size`, `@Email` 등
   - `@Valid` 어노테이션 사용

2. **예외 처리 (Exception Handling)**
   - `@ControllerAdvice` 사용
   - 커스텀 예외 클래스

3. **DTO 패턴** ✅ (구현 완료)
   - Request/Response DTO 분리
   - Entity와 DTO 매핑
   - Service 계층에서 변환 처리

4. **페이징 및 정렬** ✅ (구현 완료)
   - `Pageable` 사용
   - `Page<T>` 반환
   - 정렬 기능

5. **보안 (Spring Security)**
   - 인증/인가
   - JWT 토큰

---

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Data JPA 문서](https://spring.io/projects/spring-data-jpa)
- [Swagger/OpenAPI 문서](https://swagger.io/specification/)

---

**작성일**: 2026-01-20  
**최종 수정일**: 2026-01-20  
**프로젝트**: springofmsc  
**Spring Boot 버전**: 3.3.5  
**Java 버전**: 21

## 최근 업데이트 내역

### 2026-01-20
- ✅ **표준 계층 구조로 리팩토링**
  - Service 계층 추가 (UserService, FileService)
  - DTO 패턴 적용 (UserRequestDTO, UserResponseDTO)
  - Controller → Service → Repository 구조로 변경
  - Entity와 DTO 분리
  - 트랜잭션 관리 (`@Transactional`)
- ✅ 페이징 처리 기능 추가
  - 전체 사용자 조회 페이징 지원
  - 이름 검색 페이징 지원
  - 나이 검색 페이징 지원
  - 복합 조건 검색 페이징 지원
- ✅ Repository 메서드 통합
  - `searchUsers`와 `searchUsersWithPaging` 통합
  - 쿼리 중복 제거
- ✅ 사용자 30명 추가 스크립트 생성 (`add-30-users.sql`)
- ✅ 정렬 기능 추가 (sort 파라미터)
