package com.tunit.domain.contract.dto;

import com.tunit.common.util.KoreanDayOfWeekUtil;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public record ContractExcelSaveDto(
        String studentName,
        String phone,
        List<ContractScheduleDto> schedules, // 요일별 스케줄 리스트
        LessonSubCategory lesson,
        LocalDate firstLessonDate,
        ReservationStatus reservationStatus,
        ContractType contractType,
        Integer weekCount, // 주당 횟수 (정규는 실제 주당 횟수, 선착순/체험은 1)
        Integer unitPrice, // 단가 (모든 계약 유형에서 사용)

        // 선택
        String level, // 학생 레벨
        String place, // 레슨 장소
        String emergencyContact,
        String memo
) {
    /**
     * 주 횟수는 schedules.size()로 자동 계산 가능 (weekCount와 동일할 수 있음)
     */
    public Integer getWeekCount() {
        return schedules != null ? schedules.size() : null;
    }

    /**
     * 첫 번째 스케줄의 시작 시간 (하위 호환성)
     */
    public LocalTime getFirstStartTime() {
        return schedules != null && !schedules.isEmpty() ? schedules.get(0).startTime() : null;
    }

    /**
     * 모든 요일 번호 세트 반환 (하위 호환성)
     */
    public Set<Integer> getDayOfWeekSet() {
        if (schedules == null) return Set.of();
        return schedules.stream()
                .map(dto -> KoreanDayOfWeekUtil.getDayOfWeekNum(dto.dayOfWeek()))
                .collect(toSet());
    }
}
