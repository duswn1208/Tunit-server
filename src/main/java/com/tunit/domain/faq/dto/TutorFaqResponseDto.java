package com.tunit.domain.faq.dto;

import com.tunit.domain.faq.entity.TutorFaq;
import lombok.Getter;

@Getter
public class TutorFaqResponseDto {
    private final Long tutorFaqNo;
    private final String title;
    private final String content;
    private final boolean isExposed;
    private final int displayOrder;

    public TutorFaqResponseDto(TutorFaq tutorFaq) {
        this.tutorFaqNo = tutorFaq.getTutorFaqNo();
        this.title = tutorFaq.getTitle();
        this.content = tutorFaq.getContent();
        this.isExposed = tutorFaq.isExposed();
        this.displayOrder = tutorFaq.getDisplayOrder();
    }
}
