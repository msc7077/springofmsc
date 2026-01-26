package com.example.springofmsc.domain.notice.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 페이징 응답 DTO
 * 
 * [페이징이란?]
 * - 많은 데이터를 한 번에 보여주지 않고, 페이지 단위로 나누어 보여주는 것
 * - 예: 1000개의 공지사항을 10개씩 100페이지로 나누어 표시
 * 
 * [왜 필요한가?]
 * - 성능: 한 번에 모든 데이터를 가져오지 않아서 빠름
 * - 사용자 경험: 필요한 페이지만 로드하여 빠름
 * - 서버 부하: 필요한 데이터만 조회하여 부하 감소
 * 
 * [포함 정보]
 * - content: 실제 데이터 목록
 * - totalElements: 전체 데이터 개수
 * - totalPages: 전체 페이지 개수
 * - currentPage: 현재 페이지 번호
 * - pageSize: 페이지 크기
 * - hasNext: 다음 페이지 존재 여부
 * - hasPrevious: 이전 페이지 존재 여부
 */
@Getter
@Setter
@NoArgsConstructor
public class PageResponseDTO<T> {

	/**
	 * 실제 데이터 목록
	 */
	private List<T> content;

	/**
	 * 전체 데이터 개수
	 * - 예: 총 1000개의 공지사항이 있으면 1000
	 */
	private Long totalElements;

	/**
	 * 전체 페이지 개수
	 * - 예: 1000개를 10개씩 보여주면 100페이지
	 */
	private Integer totalPages;

	/**
	 * 현재 페이지 번호 (0부터 시작)
	 * - 예: 첫 번째 페이지 = 0, 두 번째 페이지 = 1
	 */
	private Integer currentPage;

	/**
	 * 페이지 크기 (한 페이지에 보여줄 개수)
	 * - 예: 10 = 한 페이지에 10개씩 표시
	 */
	private Integer pageSize;

	/**
	 * 다음 페이지 존재 여부
	 * - true: 다음 페이지가 있음
	 * - false: 마지막 페이지임
	 */
	private Boolean hasNext;

	/**
	 * 이전 페이지 존재 여부
	 * - true: 이전 페이지가 있음
	 * - false: 첫 번째 페이지임
	 */
	private Boolean hasPrevious;

	/**
	 * Spring Data JPA의 Page 객체를 PageResponseDTO로 변환하는 생성자
	 * 
	 * @param page Spring Data JPA의 Page 객체
	 */
	public PageResponseDTO(Page<T> page) {
		this.content = page.getContent();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.currentPage = page.getNumber();
		this.pageSize = page.getSize();
		this.hasNext = page.hasNext();
		this.hasPrevious = page.hasPrevious();
	}
}
