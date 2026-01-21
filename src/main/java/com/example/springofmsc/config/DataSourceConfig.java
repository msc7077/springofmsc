package com.example.springofmsc.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;

/**
 * Master-Slave 데이터소스 설정
 * 
 * 동작 방식:
 * - @Transactional(readOnly = true) → Slave 사용
 * - 그 외 (쓰기 작업) → Master 사용
 */
@Configuration
@EnableConfigurationProperties({ DataSourceConfig.MasterDataSourceProperties.class,
        DataSourceConfig.SlaveDataSourceProperties.class })
public class DataSourceConfig {

    /**
     * Master 데이터소스 속성
     */
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public static class MasterDataSourceProperties {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }

    /**
     * Slave 데이터소스 속성
     */
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public static class SlaveDataSourceProperties {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }

    /**
     * Master 데이터소스 생성
     */
    @Bean(name = "masterDataSource")
    public DataSource masterDataSource(MasterDataSourceProperties properties) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }

    /**
     * Slave 데이터소스 생성
     */
    @Bean(name = "slaveDataSource")
    public DataSource slaveDataSource(SlaveDataSourceProperties properties) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }

    /**
     * 라우팅 데이터소스 생성
     * Master와 Slave를 동적으로 선택
     */
    @Bean(name = "routingDataSource")
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            @Qualifier("slaveDataSource") DataSource slaveDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        // 타겟 데이터소스 맵 설정
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER, masterDataSource);
        targetDataSources.put(DataSourceType.SLAVE, slaveDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource); // 기본값은 Master

        return routingDataSource;
    }

    /**
     * 실제 사용할 데이터소스 (Primary)
     * LazyConnectionDataSourceProxy로 감싸서 실제 쿼리 실행 시점에 데이터소스 선택
     */
    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
