package com.example.springofmsc.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 동적으로 데이터소스를 선택하는 라우팅 데이터소스
 * DataSourceContextHolder에 설정된 타입에 따라 Master 또는 Slave를 선택
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();
        
        // 설정되지 않았으면 기본적으로 Master 사용
        if (dataSourceType == null) {
            return DataSourceType.MASTER;
        }
        
        return dataSourceType;
    }
}
