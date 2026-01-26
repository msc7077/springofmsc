# DTO와 Builder 패턴 비교 가이드

## 목차
1. [DTO란?](#dto란)
2. [Builder 패턴이란?](#builder-패턴이란)
3. [DTO 사용법](#dto-사용법)
4. [Builder 패턴 사용법](#builder-패턴-사용법)
5. [비교표](#비교표)
6. [언제 무엇을 사용할까?](#언제-무엇을-사용할까)
7. [실제 예시](#실제-예시)

---

## DTO란?

### 정의
**DTO (Data Transfer Object)** = 데이터 전송 객체

- 계층 간 데이터를 전송하기 위한 객체입니다.
- 비즈니스 로직이 없고, 데이터만 담습니다.
- Entity와 분리하여 사용합니다.

### 목적
1. **보안**: 내부 필드를 숨길 수 있습니다.
2. **유연성**: 필요한 필드만 노출할 수 있습니다.
3. **독립성**: Entity 변경이 API에 영향을 주지 않습니다.
4. **명확성**: API 요청/응답 형식이 명확해집니다.

### 특징
- 단순한 데이터 컨테이너
- getter/setter 메서드
- 생성자 (필요에 따라)

---

## Builder 패턴이란?

### 정의
**Builder 패턴** = 객체 생성 패턴

- 복잡한 객체를 단계적으로 생성하는 패턴입니다.
- 필드를 하나씩 설정하고 마지막에 `build()`로 객체를 생성합니다.
- Lombok의 `@Builder` 어노테이션으로 자동 생성됩니다.

### 목적
1. **가독성**: 어떤 필드를 설정하는지 명확합니다.
2. **유연성**: 선택적 필드를 쉽게 설정할 수 있습니다.
3. **불변성**: 불변 객체를 만들 수 있습니다.
4. **확장성**: 나중에 필드 추가가 쉬워집니다.

### 특징
- 메서드 체이닝 방식
- 선택적 파라미터 지원
- 가독성이 좋음

---

## DTO 사용법

### 1. 기본 DTO (현재 방식)

```java
@Getter
@Setter
@NoArgsConstructor
public class AgencyNoticeResponseDTO {
    private Integer id;
    private String subject;
    private String content;
    
    // Entity에서 변환하는 생성자
    public AgencyNoticeResponseDTO(AgencyNotice entity) {
        this.id = entity.getId();
        this.subject = entity.getSubject();
        this.content = entity.getContent();
    }
}
```

### 2. 사용 방법

#### 방법 1: 생성자 사용
```java
// Entity에서 DTO로 변환
AgencyNotice entity = agencyNoticeRepository.findById(1);
AgencyNoticeResponseDTO dto = new AgencyNoticeResponseDTO(entity);
```

#### 방법 2: Setter 사용
```java
AgencyNoticeResponseDTO dto = new AgencyNoticeResponseDTO();
dto.setId(1);
dto.setSubject("제목");
dto.setContent("내용");
```

#### 방법 3: @ModelAttribute로 자동 바인딩 (RequestDTO)
```java
@GetMapping
public ResponseEntity<?> getNotices(
    @ModelAttribute AgencyNoticeRequestDTO requestDTO) {
    // Spring이 자동으로 쿼리 파라미터를 DTO에 바인딩
    // ?agencyId=123&page=0&size=10 → requestDTO에 자동 설정
}
```

### 3. 장점
- ✅ 단순하고 직관적
- ✅ Spring의 자동 바인딩 활용 가능
- ✅ 생성자로 Entity 변환 간단

### 4. 단점
- ❌ 필드가 많으면 Setter 사용이 번거로움
- ❌ 어떤 필드를 설정했는지 불명확할 수 있음
- ❌ 선택적 필드 처리가 복잡할 수 있음

---

## Builder 패턴 사용법

### 1. Builder가 적용된 DTO

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ← Builder 패턴 추가
public class AgencyNoticeResponseDTO {
    private Integer id;
    private String subject;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
}
```

### 2. 사용 방법

#### 방법 1: 모든 필드 설정
```java
AgencyNoticeResponseDTO dto = AgencyNoticeResponseDTO.builder()
    .id(1)
    .subject("제목")
    .content("내용")
    .writer("작성자")
    .createdAt(LocalDateTime.now())
    .build();  // ← 마지막에 build() 호출
```

#### 방법 2: 선택적 필드만 설정
```java
// 일부 필드만 설정 가능
AgencyNoticeResponseDTO dto = AgencyNoticeResponseDTO.builder()
    .id(1)
    .subject("제목")
    // content, writer는 설정 안 함 (null 또는 기본값)
    .build();
```

#### 방법 3: Entity에서 변환
```java
// Entity에서 Builder로 변환
AgencyNotice entity = agencyNoticeRepository.findById(1);
AgencyNoticeResponseDTO dto = AgencyNoticeResponseDTO.builder()
    .id(entity.getId())
    .subject(entity.getSubject())
    .content(entity.getContent())
    .writer(entity.getWriter())
    .createdAt(entity.getCreatedAt())
    .build();
```

### 3. 장점
- ✅ 가독성이 좋음 (어떤 필드를 설정하는지 명확)
- ✅ 선택적 필드 처리가 쉬움
- ✅ 필드 순서에 상관없이 설정 가능
- ✅ 불변 객체 생성 가능 (final 필드 + @Builder)

### 4. 단점
- ❌ 코드가 조금 더 길어짐
- ❌ @ModelAttribute와 함께 사용하기 어려움 (RequestDTO에는 부적합)
- ❌ 초보자에게는 복잡할 수 있음

---

## 비교표

| 항목 | DTO (생성자/Setter) | Builder 패턴 |
|------|-------------------|-------------|
| **가독성** | 보통 | ⭐⭐⭐ 좋음 |
| **단순성** | ⭐⭐⭐ 매우 단순 | 보통 |
| **선택적 필드** | 어려움 | ⭐⭐⭐ 쉬움 |
| **@ModelAttribute** | ⭐⭐⭐ 가능 | 어려움 |
| **Entity 변환** | ⭐⭐⭐ 생성자로 간단 | Builder로 설정 |
| **코드 길이** | 짧음 | 조금 길음 |
| **초보자 친화적** | ⭐⭐⭐ 매우 친화적 | 보통 |

---

## 언제 무엇을 사용할까?

### DTO (생성자/Setter)를 사용하는 경우 ✅

1. **RequestDTO** (프론트에서 받는 데이터)
   ```java
   // @ModelAttribute로 자동 바인딩되므로 Builder 불필요
   @ModelAttribute AgencyNoticeRequestDTO requestDTO
   ```

2. **Entity에서 변환** (ResponseDTO)
   ```java
   // 생성자로 간단하게 변환
   new AgencyNoticeResponseDTO(entity)
   ```

3. **필드가 적을 때** (3~5개 정도)
   ```java
   // Setter로 충분
   dto.setId(1);
   dto.setName("이름");
   ```

4. **프로젝트 초기 단계**
   - 단순하게 시작하고 나중에 필요하면 추가

### Builder 패턴을 사용하는 경우 ✅

1. **수동으로 DTO 생성** (테스트 코드 등)
   ```java
   // 테스트에서 객체 생성
   AgencyNoticeResponseDTO dto = AgencyNoticeResponseDTO.builder()
       .id(1)
       .subject("테스트 제목")
       .build();
   ```

2. **선택적 필드가 많을 때** (10개 이상)
   ```java
   // 일부만 설정하고 싶을 때
   AgencyNoticeResponseDTO.builder()
       .id(1)
       .subject("제목")
       // 나머지는 선택적으로
       .build();
   ```

3. **가독성이 중요할 때**
   ```java
   // 어떤 필드를 설정하는지 명확
   .id(1)
   .subject("제목")
   .content("내용")
   ```

4. **불변 객체가 필요할 때**
   ```java
   @Builder
   public class AgencyNoticeResponseDTO {
       private final Integer id;  // final 필드
       private final String subject;
       // build() 후에는 변경 불가
   }
   ```

---

## 실제 예시

### 예시 1: RequestDTO (Builder 불필요)

```java
// 현재 방식 (권장)
@Getter
@Setter
@NoArgsConstructor
public class AgencyNoticeRequestDTO {
    private Integer agencyId;
    private Integer page = 0;
    private Integer size = 10;
}

// Controller에서 사용
@GetMapping
public ResponseEntity<?> getNotices(
    @ModelAttribute AgencyNoticeRequestDTO requestDTO) {
    // Spring이 자동으로 바인딩
    // ?agencyId=123&page=0&size=10
}
```

**이유**: `@ModelAttribute`가 자동으로 객체를 생성하고 필드를 설정해주므로 Builder가 불필요합니다.

---

### 예시 2: ResponseDTO - Entity 변환 (생성자 권장)

```java
// 현재 방식 (권장)
@Getter
@Setter
@NoArgsConstructor
public class AgencyNoticeResponseDTO {
    private Integer id;
    private String subject;
    private String content;
    
    // Entity에서 변환하는 생성자
    public AgencyNoticeResponseDTO(AgencyNotice entity) {
        this.id = entity.getId();
        this.subject = entity.getSubject();
        this.content = entity.getContent();
    }
}

// Service에서 사용
Page<AgencyNoticeResponseDTO> responsePage = page.map(
    AgencyNoticeResponseDTO::new  // 생성자 참조
);
```

**이유**: Entity에서 DTO로 변환할 때 생성자가 가장 간단하고 명확합니다.

---

### 예시 3: ResponseDTO - 수동 생성 (Builder 유용)

```java
// Builder 패턴 적용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyNoticeResponseDTO {
    private Integer id;
    private String subject;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
}

// 테스트 코드에서 사용
AgencyNoticeResponseDTO dto = AgencyNoticeResponseDTO.builder()
    .id(1)
    .subject("테스트 제목")
    .content("테스트 내용")
    .writer("테스트 작성자")
    .createdAt(LocalDateTime.now())
    .build();
```

**이유**: 테스트 코드나 수동으로 객체를 생성할 때 Builder가 유용합니다.

---

## 현재 프로젝트 권장사항

### RequestDTO
- ✅ **Builder 없이 사용** (현재 방식 유지)
- 이유: `@ModelAttribute`로 자동 바인딩

### ResponseDTO
- ✅ **생성자 방식 유지** (현재 방식 유지)
- 이유: Entity에서 변환할 때 간단하고 명확
- ⚠️ **나중에 필요하면 Builder 추가 가능**

### PageResponseDTO
- ✅ **생성자 방식 유지** (현재 방식 유지)
- 이유: Page 객체에서 변환할 때 간단

---

## 요약

### DTO (생성자/Setter)
- **언제**: Entity 변환, @ModelAttribute 바인딩, 필드가 적을 때
- **장점**: 단순, 직관적, Spring과 잘 맞음
- **단점**: 선택적 필드 처리가 어려움

### Builder 패턴
- **언제**: 수동 객체 생성, 선택적 필드가 많을 때, 가독성이 중요할 때
- **장점**: 가독성 좋음, 선택적 필드 처리 쉬움
- **단점**: 코드가 길어짐, @ModelAttribute와 어울리지 않음

### 결론
**현재 프로젝트에서는 DTO만으로 충분합니다.**
- 나중에 필요해지면 Builder를 추가하면 됩니다.
- 초보자 기준으로는 현재 방식이 이해하기 쉽습니다.

---

## 참고

- Builder 패턴은 선택사항입니다.
- 현재 방식으로도 충분히 잘 작동합니다.
- 필요할 때 추가하는 것이 좋습니다.
