package com.example.springofmsc.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Master-Slave 데이터소스 설정 클래스
 * 
 * [역할]
 * - application.yaml의 설정값을 읽어서 Master와 Slave 데이터소스를 생성합니다.
 * - 두 데이터소스를 RoutingDataSource로 연결하여 자동 선택이 가능하도록 합니다.
 * 
 * [@Configuration이란?]
 * - 이 클래스가 Spring 설정 클래스임을 나타냅니다.
 * - @Bean 메서드들이 자동으로 실행되어 Spring 컨테이너에 등록됩니다.
 * 
 * [Bean 생성 순서]
 * 1. masterDataSource() → Master DB 연결 정보 생성
 * 2. slaveDataSource() → Slave DB 연결 정보 생성
 * 3. routingDataSource() → Master/Slave를 선택하는 라우터 생성
 * 4. dataSource() → 실제로 사용할 최종 데이터소스 생성
 */
@Configuration
public class DataSourceConfig {

	/**
	 * Environment: application.yaml의 모든 설정값을 읽을 수 있는 Spring 객체
	 * 
	 * [왜 사용하나?]
	 * - @ConfigurationProperties를 Bean 메서드에 직접 사용하면 Spring Boot 3.x에서 바인딩이 실패할 수
	 * 있습니다.
	 * - Environment를 사용하면 확실하게 설정값을 읽을 수 있습니다.
	 */
	private final Environment environment;

	/**
	 * 생성자 주입: Spring이 Environment를 자동으로 주입해줍니다.
	 */
	public DataSourceConfig(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Master 데이터소스 생성
	 * 
	 * [@Bean이란?]
	 * - 이 메서드가 반환하는 객체를 Spring 컨테이너에 등록합니다.
	 * - 다른 클래스에서 @Autowired로 주입받아 사용할 수 있습니다.
	 * 
	 * [name = "masterDataSource"]
	 * - Bean의 이름을 명시적으로 지정합니다.
	 * - 다른 Bean에서 이 이름으로 참조할 수 있습니다.
	 * 
	 * [HikariDataSource란?]
	 * - HikariCP라는 고성능 커넥션 풀 라이브러리입니다.
	 * - Spring Boot의 기본 데이터소스입니다.
	 * - DB 연결을 미리 만들어두고 재사용하여 성능을 향상시킵니다.
	 * 
	 * [동작 방식]
	 * 1. application.yaml에서 "spring.datasource.master.*" 설정값 읽기
	 * 2. HikariDataSource 객체 생성
	 * 3. 각 설정값을 데이터소스에 설정
	 * 4. 설정된 데이터소스 반환
	 * 
	 * @return Master DB 연결 정보가 설정된 DataSource
	 */
	@Bean(name = "masterDataSource")
	public DataSource masterDataSource() {
		// HikariCP 데이터소스 객체 생성
		HikariDataSource dataSource = new HikariDataSource();

		// application.yaml의 설정값을 읽어서 데이터소스에 설정
		// 예: spring.datasource.master.url → "jdbc:mysql://127.0.0.1:33061/..."
		dataSource.setJdbcUrl(environment.getProperty("spring.datasource.master.url"));
		dataSource.setUsername(environment.getProperty("spring.datasource.master.username"));
		dataSource.setPassword(environment.getProperty("spring.datasource.master.password"));
		dataSource.setDriverClassName(environment.getProperty("spring.datasource.master.driver-class-name"));

		return dataSource;
	}

	/**
	 * Slave 데이터소스 생성
	 * 
	 * [Master와의 차이점]
	 * - 설정값만 다르고 로직은 동일합니다.
	 * - "spring.datasource.slave.*" 설정값을 읽습니다.
	 * 
	 * @return Slave DB 연결 정보가 설정된 DataSource
	 */
	@Bean(name = "slaveDataSource")
	public DataSource slaveDataSource() {
		// HikariCP 데이터소스 객체 생성
		HikariDataSource dataSource = new HikariDataSource();

		// application.yaml의 설정값을 읽어서 데이터소스에 설정
		// 예: spring.datasource.slave.url → "jdbc:mysql://127.0.0.1:33062/..."
		dataSource.setJdbcUrl(environment.getProperty("spring.datasource.slave.url"));
		dataSource.setUsername(environment.getProperty("spring.datasource.slave.username"));
		dataSource.setPassword(environment.getProperty("spring.datasource.slave.password"));
		dataSource.setDriverClassName(environment.getProperty("spring.datasource.slave.driver-class-name"));

		return dataSource;
	}

	/**
	 * RoutingDataSource 생성
	 * 
	 * [역할]
	 * - Master와 Slave 데이터소스를 맵에 등록합니다.
	 * - RoutingDataSource가 "master" 또는 "slave" 키를 받으면 해당 데이터소스를 반환합니다.
	 * 
	 * [@DependsOn이란?]
	 * - 이 Bean이 생성되기 전에 먼저 생성되어야 할 Bean을 지정합니다.
	 * - masterDataSource와 slaveDataSource가 먼저 생성되어야 routingDataSource를 만들 수 있습니다.
	 * 
	 * [동작 방식]
	 * 1. Master와 Slave 데이터소스를 맵에 등록 ("master" → Master DB, "slave" → Slave DB)
	 * 2. RoutingDataSource에 맵 전달
	 * 3. 기본 데이터소스를 Master로 설정 (키를 찾지 못할 때 사용)
	 * 4. RoutingDataSource 반환
	 * 
	 * [왜 맵을 사용하나?]
	 * - RoutingDataSource가 "master" 키를 받으면 → Master DB 반환
	 * - RoutingDataSource가 "slave" 키를 받으면 → Slave DB 반환
	 * - 키-값 쌍으로 관리하기 쉽습니다.
	 * 
	 * @return Master/Slave를 선택할 수 있는 RoutingDataSource
	 */
	@Bean
	@DependsOn({ "masterDataSource", "slaveDataSource" })
	public DataSource routingDataSource() {
		// RoutingDataSource 객체 생성 (AbstractRoutingDataSource를 상속받은 클래스)
		RoutingDataSource routingDataSource = new RoutingDataSource();

		// Master와 Slave 데이터소스를 맵에 등록
		// 키: "master", "slave" (RoutingDataSource.determineCurrentLookupKey()가 반환하는 값과
		// 일치해야 함)
		// 값: 실제 데이터소스 객체
		Map<Object, Object> dataSourceMap = new HashMap<>();
		dataSourceMap.put("master", masterDataSource()); // "master" 키 → Master DB
		dataSourceMap.put("slave", slaveDataSource()); // "slave" 키 → Slave DB

		// RoutingDataSource에 데이터소스 맵 등록
		// 이제 RoutingDataSource가 "master" 또는 "slave" 키를 받으면 해당 데이터소스를 반환합니다.
		routingDataSource.setTargetDataSources(dataSourceMap);

		// 기본 데이터소스를 Master로 설정
		// 키를 찾지 못하거나 설정이 안 되어 있을 때 Master를 사용합니다.
		routingDataSource.setDefaultTargetDataSource(masterDataSource());

		return routingDataSource;
	}

	/**
	 * 실제로 사용할 데이터소스 (최종 데이터소스)
	 * 
	 * [@Primary란?]
	 * - 여러 DataSource Bean이 있을 때, 이걸 기본으로 사용하라는 의미입니다.
	 * - JPA나 다른 곳에서 @Autowired로 DataSource를 주입받을 때 이 Bean이 사용됩니다.
	 * 
	 * [LazyConnectionDataSourceProxy란?]
	 * - 실제 DB 연결을 지연시키는 프록시입니다.
	 * - 트랜잭션이 시작되고 실제 쿼리가 실행될 때까지 연결을 미룹니다.
	 * - 이렇게 해야 RoutingDataSource가 올바른 시점에 Master/Slave를 선택할 수 있습니다.
	 * 
	 * [왜 LazyConnectionDataSourceProxy로 감싸나?]
	 * - 만약 바로 연결하면, 트랜잭션 시작 시점에 데이터소스가 결정됩니다.
	 * - 하지만 우리는 쿼리 실행 시점에 Master/Slave를 선택하고 싶습니다.
	 * - LazyConnectionDataSourceProxy가 연결을 지연시켜서, 실제 쿼리 실행 시점에 선택할 수 있게 해줍니다.
	 * 
	 * [@DependsOn("routingDataSource")]
	 * - routingDataSource가 먼저 생성되어야 합니다.
	 * 
	 * [동작 흐름]
	 * 1. JPA가 DataSource가 필요함
	 * 2. 이 Bean(dataSource)을 주입받음
	 * 3. LazyConnectionDataSourceProxy가 연결을 지연시킴
	 * 4. 실제 쿼리 실행 시점에 RoutingDataSource가 Master/Slave 선택
	 * 5. 선택된 데이터소스로 연결
	 * 
	 * @return 최종적으로 사용할 DataSource (LazyConnectionDataSourceProxy로 감싼
	 *         RoutingDataSource)
	 */
	@Bean
	@Primary // 여러 DataSource 중 기본으로 사용
	@DependsOn("routingDataSource") // routingDataSource가 먼저 생성되어야 함
	public DataSource dataSource() {
		// LazyConnectionDataSourceProxy로 감싸서 실제 쿼리 실행 시점에 연결 결정
		return new LazyConnectionDataSourceProxy(routingDataSource());
	}
}
