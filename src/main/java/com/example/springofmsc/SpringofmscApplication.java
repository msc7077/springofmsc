package com.example.springofmsc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring Boot 메인 애플리케이션 클래스
 * 
 * [@SpringBootApplication]
 * - Spring Boot 애플리케이션임을 나타냅니다.
 * - @Configuration, @EnableAutoConfiguration, @ComponentScan을 포함합니다.
 * 
 * [@EnableJpaAuditing]
 * - JPA Auditing 기능을 활성화합니다.
 * - @CreatedDate, @LastModifiedDate가 자동으로 동작하도록 해줍니다.
 * - BaseEntity의 createdAt, updatedAt이 자동으로 관리됩니다.
 * 
 * [JPA Auditing이란?]
 * - 엔티티의 생성일시, 수정일시를 자동으로 관리하는 기능입니다.
 * - 수동으로 설정할 필요 없이 자동으로 처리됩니다.
 */
@EnableJpaAuditing
@SpringBootApplication
public class SpringofmscApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringofmscApplication.class, args);
	}

}
