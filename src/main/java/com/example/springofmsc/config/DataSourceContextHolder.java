package com.example.springofmsc.config;

/**
 * 현재 스레드(요청)가 어떤 데이터소스를 사용할지 저장하는 클래스
 * 
 * [왜 필요한가?]
 * - 여러 사용자가 동시에 요청을 보낼 때, 각 요청마다 다른 데이터소스를 사용할 수 있어야 합니다.
 * - 예: 사용자 A는 읽기 작업(Slave), 사용자 B는 쓰기 작업(Master)을 동시에 할 수 있어야 합니다.
 * 
 * [ThreadLocal이란?]
 * - 각 스레드(요청)마다 독립적인 저장 공간을 제공합니다.
 * - 스레드 A의 값과 스레드 B의 값이 서로 섞이지 않습니다.
 * - 전역 변수처럼 보이지만, 실제로는 각 스레드마다 별도의 값을 가집니다.
 * 
 * [동작 예시]
 * - 요청 1 (스레드 A): setDataSourceType(SLAVE) → 스레드 A만 SLAVE 사용
 * - 요청 2 (스레드 B): setDataSourceType(MASTER) → 스레드 B만 MASTER 사용
 * - 두 요청이 동시에 실행되어도 서로 영향을 주지 않습니다.
 */
public class DataSourceContextHolder {
	
	/**
	 * ThreadLocal: 각 스레드마다 독립적인 DataSourceType을 저장하는 저장소
	 * 
	 * static final: 클래스당 하나만 존재하고, 프로그램 종료까지 유지됨
	 * ThreadLocal<DataSourceType>: 각 스레드가 DataSourceType 값을 독립적으로 저장
	 */
	private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();
	
	/**
	 * 현재 스레드에 사용할 데이터소스 타입을 설정합니다.
	 * 
	 * @param dataSourceType 설정할 데이터소스 타입 (MASTER 또는 SLAVE)
	 * 
	 * [사용 예시]
	 * DataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE);
	 * → 이 스레드에서 이후 DB 작업은 Slave를 사용합니다.
	 */
	public static void setDataSourceType(DataSourceType dataSourceType) {
		contextHolder.set(dataSourceType);
	}
	
	/**
	 * 현재 스레드가 사용할 데이터소스 타입을 조회합니다.
	 * 
	 * @return 현재 스레드의 데이터소스 타입 (기본값: MASTER)
	 * 
	 * [동작 방식]
	 * 1. contextHolder.get()으로 현재 스레드의 값 조회
	 * 2. 값이 null이면 (아직 설정 안 됨) → 기본값 MASTER 반환
	 * 3. 값이 있으면 → 그 값 반환
	 * 
	 * [왜 기본값이 MASTER인가?]
	 * - 안전을 위해: 설정이 안 되어 있으면 쓰기 가능한 Master를 사용
	 * - 읽기 전용인 Slave를 기본값으로 하면, 설정 실수 시 쓰기 작업이 실패할 수 있음
	 */
	public static DataSourceType getDataSourceType() {
		DataSourceType type = contextHolder.get();
		// 값이 없으면 기본값으로 MASTER 반환 (안전한 기본값)
		return type == null ? DataSourceType.MASTER : type;
	}
	
	/**
	 * 현재 스레드의 데이터소스 타입을 제거합니다.
	 * 
	 * [왜 필요한가?]
	 * - ThreadLocal은 스레드가 종료되어도 메모리에 남아있을 수 있습니다.
	 * - (Tomcat 같은 서버는 스레드를 재사용하기 때문)
	 * - 이전 요청의 값이 다음 요청에 영향을 주지 않도록 정리해야 합니다.
	 * 
	 * [언제 호출하나?]
	 * - DB 작업이 완료된 후 (AOP의 finally 블록에서)
	 * - 예외가 발생해도 반드시 정리해야 하므로 finally에서 호출
	 * 
	 * [이게 없으면?]
	 * - 메모리 누수 발생 가능
	 * - 이전 요청의 데이터소스 타입이 다음 요청에 영향을 줄 수 있음
	 */
	public static void clearDataSourceType() {
		contextHolder.remove();
	}
}
