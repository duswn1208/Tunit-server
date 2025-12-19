package com.tunit.domain.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TutorFaqOrderUpdateRequestDto {
    private List<FaqOrderDto> faqs;

    @Getter
    @NoArgsConstructor
    public static class FaqOrderDto {
        private Long tutorFaqNo;
        private int displayOrder;
    }
}
