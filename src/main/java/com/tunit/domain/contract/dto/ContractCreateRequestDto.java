package com.tunit.domain.contract.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tunit.common.util.LenientLocalTimeDeserializer;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractCreateRequestDto {
    private Long tutorProfileNo;
    @Setter
    private Long studentNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private List<LocalDateTime> lessonDtList; // 실제 수업 날짜/시간 리스트만 받음

    private ContractType contractType;
    private LessonSubCategory lessonCategory;
    private Integer weekCount; //주 단위 수업 횟수
    private Integer lessonCount; //총 수업 횟수
    private String lessonName;
    private String level;
    private String place; // 상세 장소
    private String emergencyContact;
    private Integer totalPrice; // 총 계약 금액
    private String memo;
    @Setter
    private ContractSource source; // 계약 신청 경로

    // 체험 레슨용 후보 시간 (신규 추가)
    private List<TrialCandidateTime> trialCandidates;

    /**
     * lessonName 자동 생성
     * - 정규/선착순레슨: "영어회화 정규레슨 (주2회)"
     * - 상담/체험레슨: "영어회화 상담/체험레슨"
     */
    public String generateLessonName() {
        return generateLessonName(lessonCategory, contractType, weekCount, lessonName);
    }

    public static String generateLessonName(LessonSubCategory lessonCategory, ContractType contractType, Integer weekCount, String lessonName) {
        if (lessonName != null && !lessonName.isBlank()) {
            return lessonName;
        }

        String categoryLabel = lessonCategory.getLabel();
        String typeLabel = contractType.getLabel();

        if ((contractType.isRegular() || contractType.isFirstCome()) && weekCount != null) {
            return categoryLabel + " " + typeLabel + " (주" + weekCount + "회)";
        }

        return categoryLabel + " " + typeLabel;
    }

    public boolean isTrial() {
        return contractType != null && contractType == ContractType.TRIAL;
    }

    public boolean hasTrialCandidates() {
        return trialCandidates != null && !trialCandidates.isEmpty();
    }

    // Inner class 추가
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrialCandidateTime {
        private Integer priority;  // 1, 2, 3

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate candidateDate;

        @JsonFormat(pattern = "HH:mm")
        @JsonDeserialize(using = LenientLocalTimeDeserializer.class)
        private LocalTime candidateStartTime;
    }
}
