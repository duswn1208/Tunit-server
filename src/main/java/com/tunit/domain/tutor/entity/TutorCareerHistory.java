package com.tunit.domain.tutor.entity;

import com.tunit.domain.tutor.define.TutorCareerHistoryType;
import com.tunit.domain.tutor.dto.TutorCareerHistorySaveDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class TutorCareerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorCareerHistoryNo;

    private Long tutorProfileNo;

    @Enumerated(EnumType.STRING)
    private TutorCareerHistoryType type;

    private String title;       // 학교명 / 자격증명 / 수상명 / 회사명
    private String subTitle;    // 전공 / 발급기관 / 주관기관 / 직책
    private LocalDate startDate;
    private LocalDate endDate;  // null = 현재 재직/재학 중
    private String description;
    private Integer displayOrder;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public TutorCareerHistory(Long tutorCareerHistoryNo, Long tutorProfileNo, TutorCareerHistoryType type,
                               String title, String subTitle, LocalDate startDate, LocalDate endDate,
                               String description, Integer displayOrder,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorCareerHistoryNo = tutorCareerHistoryNo;
        this.tutorProfileNo = tutorProfileNo;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TutorCareerHistory saveFrom(Long tutorProfileNo, TutorCareerHistorySaveDto dto) {
        return TutorCareerHistory.of()
                .tutorProfileNo(tutorProfileNo)
                .type(dto.getType())
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
