package com.example.springofmsc.config;

/**
 * 현재 사용할 데이터소스를 저장하는 ThreadLocal
 * 각 스레드마다 독립적으로 데이터소스 타입을 저장
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

    /**
     * 현재 스레드에 사용할 데이터소스 타입 설정
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    /**
     * 현재 스레드에 설정된 데이터소스 타입 조회
     */
    public static DataSourceType getDataSourceType() {
        return contextHolder.get();
    }

    /**
     * 현재 스레드의 데이터소스 타입 제거
     */
    public static void clearDataSourceType() {
        contextHolder.remove();
    }
}
