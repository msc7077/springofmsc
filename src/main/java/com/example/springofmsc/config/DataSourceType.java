package com.example.springofmsc.config;

/**
 * 데이터소스 타입
 * Master-Slave 구분을 위한 enum
 */
public enum DataSourceType {
    MASTER,  // 쓰기용 (Master)
    SLAVE    // 읽기용 (Slave)
}
