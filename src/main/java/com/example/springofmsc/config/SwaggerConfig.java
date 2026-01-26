package com.example.springofmsc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Swagger 설정 클래스
 * 
 * [Swagger란?]
 * - API 문서를 자동으로 생성해주는 도구입니다.
 * - 브라우저에서 API를 테스트할 수 있는 UI를 제공합니다.
 * - API 엔드포인트, 파라미터, 응답 형식 등을 자동으로 문서화합니다.
 * 
 * [@Configuration]
 * - 이 클래스가 Spring 설정 클래스임을 나타냅니다.
 * - @Bean 메서드들이 자동으로 실행되어 Spring 컨테이너에 등록됩니다.
 * 
 * [접속 방법]
 * - 애플리케이션 실행 후 브라우저에서 접속:
 * http://localhost:8080/swagger-ui.html
 * 또는
 * http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

	/**
	 * OpenAPI 설정
	 * 
	 * [@Bean]
	 * - 이 메서드가 반환하는 객체를 Spring 컨테이너에 등록합니다.
	 * - Swagger가 이 설정을 사용해서 API 문서를 생성합니다.
	 * 
	 * [OpenAPI란?]
	 * - OpenAPI 3.0 스펙을 따르는 API 문서 형식입니다.
	 * - Swagger UI가 이 정보를 읽어서 문서를 표시합니다.
	 * 
	 * @return OpenAPI 설정 객체
	 */
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Spring Boot API") // API 문서 제목
						.description("Spring Boot를 사용한 REST API 문서") // API 문서 설명
						.version("v1.0")); // API 버전
	}
}
