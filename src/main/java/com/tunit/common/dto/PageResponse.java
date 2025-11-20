package com.tunit.common.dto;

import java.util.List;

/**
 * 페이징 응답 공통 DTO
 */
public record PageResponse<T>(
        List<T> content,        // 실제 데이터 리스트
        PageInfo pageInfo       // 페이지 정보
) {
    public record PageInfo(
            int currentPage,    // 현재 페이지 (0부터 시작)
            int pageSize,       // 페이지 크기
            long totalElements, // 전체 요소 개수
            int totalPages,     // 전체 페이지 수
            boolean hasNext,    // 다음 페이지 존재 여부
            boolean hasPrevious // 이전 페이지 존재 여부
    ) {}

    public static <T> PageResponse<T> of(
            List<T> content,
            int currentPage,
            int pageSize,
            long totalElements
    ) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        boolean hasNext = currentPage < totalPages - 1;
        boolean hasPrevious = currentPage > 0;

        PageInfo pageInfo = new PageInfo(
                currentPage,
                pageSize,
                totalElements,
                totalPages,
                hasNext,
                hasPrevious
        );

        return new PageResponse<>(content, pageInfo);
    }

    public static <T> PageResponse<T> empty(int pageSize) {
        return of(List.of(), 0, pageSize, 0);
    }
}

