package com.tunit.domain.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TutorFaqCreateRequestDto {
    private String title;
    private String content;
}

