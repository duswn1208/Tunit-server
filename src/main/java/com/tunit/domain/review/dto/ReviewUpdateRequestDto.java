package com.tunit.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 수정 요청 DTO
 */
public record ReviewUpdateRequestDto(
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다")
        Integer rating,

        @Size(max = 1000, message = "리뷰 내용은 1000자 이하여야 합니다")
        String content
) {
}

