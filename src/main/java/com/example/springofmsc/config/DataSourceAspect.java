package com.example.springofmsc.config;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP를 사용해서 읽기/쓰기 작업을 자동으로 구분하는 클래스
 * 
 * [AOP란?]
 * - Aspect-Oriented Programming (관점 지향 프로그래밍)
 * - 특정 메서드 실행 전/후에 자동으로 코드를 실행할 수 있게 해줍니다.
 * - 예: @Transactional이 붙은 메서드를 자동으로 가로채서 처리
 * 
 * [이 클래스의 역할]
 * - Service 메서드에 @Transactional이 붙어있는지 확인
 * - readOnly = true면 → Slave 사용
 * - readOnly = false 또는 없으면 → Master 사용
 * 
 * [왜 필요한가?]
 * - Service 메서드에서 매번 수동으로 Master/Slave를 선택하는 것은 번거롭고 실수하기 쉽습니다.
 * - @Transactional(readOnly = true)만 붙이면 자동으로 Slave를 사용하도록 해줍니다.
 * - 코드가 깔끔해지고 유지보수가 쉬워집니다.
 * 
 * [동작 예시]
 * ```java
 * 
 * @Transactional(readOnly = true) // ← 이 어노테이션을 보고
 *                         public List<User> findAll() {
 *                         return userRepository.findAll(); // ← 자동으로 Slave 사용
 *                         }
 *                         ```
 * 
 *                         [@Aspect]
 *                         - 이 클래스가 AOP 클래스임을 나타냅니다.
 * 
 *                         [@Component]
 *                         - Spring이 이 클래스를 Bean으로 등록합니다.
 * 
 *                         [@Order(0)]
 *                         - 다른 AOP보다 먼저 실행되도록 설정합니다.
 *                         - 데이터소스 선택이 다른 작업보다 먼저 이루어져야 하기 때문입니다.
 */
@Aspect
@Component
@Order(0) // 다른 AOP보다 먼저 실행 (데이터소스 선택이 우선)
public class DataSourceAspect {

	/**
	 * @Transactional이 붙은 메서드를 가로채서 읽기/쓰기를 구분합니다.
	 * 
	 *                 [@Around란?]
	 *                 - 메서드 실행 전과 후에 모두 코드를 실행할 수 있습니다.
	 *                 - 메서드 실행 전: 데이터소스 타입 설정
	 *                 - 메서드 실행: 원래 메서드 실행
	 *                 - 메서드 실행 후: 데이터소스 타입 정리 (finally 블록)
	 * 
	 *                 [@annotation(...)]
	 *                 - @Transactional 어노테이션이 붙은 메서드만 가로챕니다.
	 * 
	 *                 [ProceedingJoinPoint란?]
	 *                 - 가로챈 메서드의 정보를 담고 있는 객체입니다.
	 *                 - joinPoint.proceed()로 원래 메서드를 실행할 수 있습니다.
	 * 
	 *                 [동작 흐름]
	 *                 1. @Transactional이 붙은 메서드 호출
	 *                 2. 이 메서드가 실행됨 (가로챔)
	 *                 3. 메서드의 @Transactional 어노테이션 확인
	 *                 4. readOnly = true면 SLAVE, 아니면 MASTER 설정
	 *                 5. DataSourceContextHolder에 설정값 저장
	 *                 6. 원래 메서드 실행 (joinPoint.proceed())
	 *                 7. finally 블록에서 DataSourceContextHolder 정리 (메모리 누수 방지)
	 * 
	 *                 [try-finally를 사용하는 이유]
	 *                 - try: 메서드 실행 전 데이터소스 타입 설정
	 *                 - finally: 메서드 실행 후 반드시 정리 (예외가 발생해도 실행됨)
	 *                 - 메모리 누수를 방지하기 위해 반드시 정리해야 합니다.
	 * 
	 * @param joinPoint 가로챈 메서드의 정보
	 * @return 원래 메서드의 반환값
	 * @throws Throwable 원래 메서드에서 발생한 예외
	 */
	@Around("@annotation(org.springframework.transaction.annotation.Transactional)")
	public Object determineDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			// 1. 가로챈 메서드의 정보 가져오기
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();

			// 2. 메서드에 붙은 @Transactional 어노테이션 가져오기
			Transactional transactional = method.getAnnotation(Transactional.class);

			// 3. readOnly 속성 확인하여 데이터소스 타입 결정
			// readOnly = true → 읽기 작업 → Slave 사용
			// readOnly = false 또는 없음 → 쓰기 작업 → Master 사용
			if (transactional != null && transactional.readOnly()) {
				// 읽기 작업: Slave 사용
				DataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE);
			} else {
				// 쓰기 작업: Master 사용
				DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
			}

			// 4. 원래 메서드 실행 (이때 DB 연결이 필요하면 위에서 설정한 데이터소스 사용)
			return joinPoint.proceed();

		} finally {
			// 5. 작업 완료 후 반드시 정리 (메모리 누수 방지)
			// 예외가 발생해도 실행되도록 finally 블록에 작성
			// 이전 요청의 데이터소스 타입이 다음 요청에 영향을 주지 않도록
			DataSourceContextHolder.clearDataSourceType();
		}
	}
}
