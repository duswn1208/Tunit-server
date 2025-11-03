package com.tunit.domain.contract.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.*;

import java.time.LocalDateTime;
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
    private List<LocalDateTime> lessonDtList; //수업 날짜 및 시간 리스트(선착순, 상담레슨은 1개, 정규레슨은 주 횟수 * 4)

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

    /**
     * lessonName 자동 생성
     * - 고정레슨: "영어회화 고정레슨 (주2회)"
     * - 주간레슨/패키지: "영어회화 주간레슨"
     */
    public String generateLessonName() {
        if (lessonName != null && !lessonName.isBlank()) {
            return lessonName;
        }

        String categoryLabel = lessonCategory.getLabel();
        String typeLabel = contractType.getLabel();

        // REGULAR(고정레슨)인 경우 주 횟수 포함
        if (contractType.isRegular() && weekCount != null) {
            return categoryLabel + " " + typeLabel + " (주" + weekCount + "회)";
        }

        // 그 외(주간레슨, 패키지)는 타입만 포함
        return categoryLabel + " " + typeLabel;
    }
}
