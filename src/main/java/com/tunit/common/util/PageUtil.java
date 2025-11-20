package com.tunit.common.util;

import com.tunit.common.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 유틸리티 클래스
 */
public class PageUtil {

    private PageUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Spring Data의 Page를 PageResponse로 변환
     */
    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    /**
     * 리스트와 페이지 정보로 PageResponse 생성
     */
    public static <T> PageResponse<T> createPageResponse(
            List<T> content,
            int currentPage,
            int pageSize,
            long totalElements
    ) {
        return PageResponse.of(content, currentPage, pageSize, totalElements);
    }

    /**
     * 빈 PageResponse 생성
     */
    public static <T> PageResponse<T> emptyPage(int pageSize) {
        return PageResponse.empty(pageSize);
    }
}

