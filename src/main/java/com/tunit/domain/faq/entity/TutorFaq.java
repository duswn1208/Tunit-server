package com.tunit.domain.faq.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tutor_faq")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorFaq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tutor_faq_no")
    private Long tutorFaqNo;

    @Column(name = "tutor_profile_no", nullable = false)
    private Long tutorProfileNo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean isExposed = true;

    @Builder
    public TutorFaq(Long tutorProfileNo, String title, String content, int displayOrder, boolean isExposed) {
        this.tutorProfileNo = tutorProfileNo;
        this.title = title;
        this.content = content;
        this.displayOrder = displayOrder;
        this.isExposed = isExposed;
    }

    public void update(String question, String answer, boolean isExposed) {
        this.title = question;
        this.content = answer;
        this.isExposed = isExposed;
    }

    public void updateOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
