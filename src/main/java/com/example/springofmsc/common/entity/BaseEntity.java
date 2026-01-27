package com.example.springofmsc.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * BaseEntity - 공통 필드를 관리하는 추상 클래스
 * 
 * [BaseEntity란?]
 * - 모든 Entity가 공통으로 가지는 필드를 정의하는 클래스입니다.
 * - createdAt(생성일시), updatedAt(수정일시)를 자동으로 관리합니다.
 * 
 * [@MappedSuperclass]
 * - 이 클래스는 테이블로 생성되지 않습니다.
 * - 다른 Entity가 상속받아서 필드를 사용할 수 있게 해줍니다.
 * - 예: AgencyNotice extends BaseEntity → AgencyNotice 테이블에 createdAt, updatedAt 컬럼 생성
 * 
 * [@EntityListeners(AuditingEntityListener.class)]
 * - JPA Auditing 기능을 활성화합니다.
 * - @CreatedDate, @LastModifiedDate가 자동으로 동작하도록 해줍니다.
 * 
 * [@CreatedDate]
 * - 엔티티가 처음 저장될 때 자동으로 현재 시간이 설정됩니다.
 * - 수동으로 설정할 필요가 없습니다.
 * 
 * [@LastModifiedDate]
 * - 엔티티가 수정될 때마다 자동으로 현재 시간이 업데이트됩니다.
 * - 수동으로 업데이트할 필요가 없습니다.
 * 
 * [@Getter]
 * - getter 메서드만 생성 (setter는 자동으로 설정되므로 불필요)
 * 
 * [사용 방법]
 * ```java
 * @Entity
 * public class AgencyNotice extends BaseEntity {
 *     // createdAt, updatedAt은 BaseEntity에서 상속받음
 *     // 자동으로 관리됨
 * }
 * ```
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

	/**
	 * 생성일시
	 * 
	 * [@CreatedDate]
	 * - 엔티티가 처음 저장될 때 자동으로 현재 시간 설정
	 * - 예: save() 호출 시 자동으로 현재 시간이 들어감
	 * 
	 * [@Column(updatable = false)]
	 * - 한 번 설정되면 수정 불가
	 * - 생성일시는 변경되면 안 되므로 false
	 */
	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	/**
	 * 수정일시
	 * 
	 * [@LastModifiedDate]
	 * - 엔티티가 수정될 때마다 자동으로 현재 시간 업데이트
	 * - 예: save() 호출 시 자동으로 현재 시간이 업데이트됨
	 */
	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
