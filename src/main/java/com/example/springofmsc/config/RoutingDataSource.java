package com.example.springofmsc.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Master-Slave 데이터소스를 자동으로 선택하는 클래스
 * 
 * [역할]
 * - Spring이 DB 연결이 필요할 때, "어떤 DB를 사용할까?"를 결정하는 클래스입니다.
 * - DataSourceContextHolder에 저장된 값(MASTER 또는 SLAVE)을 보고 적절한 DB를 선택합니다.
 * 
 * [AbstractRoutingDataSource란?]
 * - Spring이 제공하는 추상 클래스입니다.
 * - 여러 데이터소스 중 하나를 선택하는 로직을 구현할 수 있게 해줍니다.
 * - determineCurrentLookupKey() 메서드만 구현하면 자동으로 라우팅됩니다.
 * 
 * [동작 흐름]
 * 1. JPA가 DB 연결이 필요함
 * 2. Spring이 RoutingDataSource의 determineCurrentLookupKey() 호출
 * 3. DataSourceContextHolder에서 현재 타입 조회 (MASTER 또는 SLAVE)
 * 4. "master" 또는 "slave" 문자열 반환
 * 5. Spring이 해당 키에 맞는 데이터소스(Master 또는 Slave)를 사용
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

	/**
	 * 현재 사용할 데이터소스의 키를 결정합니다.
	 * 
	 * [Spring이 이 메서드를 호출하는 시점]
	 * - DB 연결이 필요한 순간 (쿼리 실행 전)
	 * - 트랜잭션이 시작될 때
	 * 
	 * [반환값]
	 * - "master": Master DB 사용
	 * - "slave": Slave DB 사용
	 * 
	 * [주의사항]
	 * - 이 메서드가 반환하는 문자열("master", "slave")은
	 * DataSourceConfig에서 등록한 맵의 키와 정확히 일치해야 합니다.
	 * 
	 * @return "master" 또는 "slave" 문자열
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		// 1. 현재 스레드가 사용할 데이터소스 타입 조회
		DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();

		// 2. MASTER면 "master", 아니면 "slave" 반환
		// (DataSourceConfig에서 등록한 맵의 키와 일치해야 함)
		if (dataSourceType == DataSourceType.MASTER) {
			return "master";
		} else {
			return "slave";
		}
	}
}
