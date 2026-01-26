package com.example.springofmsc.config;

/**
 * 데이터소스 타입을 구분하는 enum (열거형)
 * 
 * Master-Slave 구조에서 어떤 데이터베이스를 사용할지 구분하기 위한 타입입니다.
 * 
 * - MASTER: 쓰기 작업(INSERT, UPDATE, DELETE)을 처리하는 메인 데이터베이스
 * - SLAVE: 읽기 작업(SELECT)을 처리하는 복제 데이터베이스
 * 
 * 이 enum은 다른 클래스들에서 "어떤 DB를 사용할까?"를 결정할 때 사용됩니다.
 */
public enum DataSourceType {
	/** 쓰기 작업용 메인 데이터베이스 */
	MASTER,
	
	/** 읽기 작업용 복제 데이터베이스 */
	SLAVE
}
