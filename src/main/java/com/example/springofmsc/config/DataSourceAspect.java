package com.example.springofmsc.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 트랜잭션 어노테이션을 읽어서 자동으로 데이터소스 선택
 * 
 * @Transactional(readOnly = true) → Slave
 *                         그 외 → Master
 */
@Aspect
@Component
@Order(0)
public class DataSourceAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional) || " +
            "@within(org.springframework.transaction.annotation.Transactional)")
    public Object determineDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            // 메서드 레벨의 @Transactional 확인
            Transactional transactional = signature.getMethod().getAnnotation(Transactional.class);

            // 메서드에 없으면 클래스 레벨 확인
            if (transactional == null) {
                transactional = signature.getMethod().getDeclaringClass().getAnnotation(Transactional.class);
            }

            // readOnly가 true이면 Slave, 아니면 Master
            if (transactional != null && transactional.readOnly()) {
                DataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE);
            } else {
                DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
            }

            return joinPoint.proceed();
        } finally {
            // 사용 후 정리
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
