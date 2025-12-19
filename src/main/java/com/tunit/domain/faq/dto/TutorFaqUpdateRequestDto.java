package com.tunit.domain.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TutorFaqUpdateRequestDto {
    private String title;
    private String content;
    private boolean isExposed;
}

