package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.dto.ContractExcelSaveDto;
import com.tunit.domain.contract.dto.ContractScheduleDto;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ContractExcelSaveDto 단위 테스트
 */
@DisplayName("ContractExcelSaveDto 테스트")
class ContractExcelSaveDtoTest {

    @Test
    @DisplayName("getWeekCount() - 요일 개수로 주 횟수 계산")
    void getWeekCount_CalculatesFromSchedules() {
        // given
        List<ContractScheduleDto> schedules = List.of(
                new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0)),
                new ContractScheduleDto(3, "수", LocalTime.of(14, 0), LocalTime.of(15, 0)),
                new ContractScheduleDto(5, "금", LocalTime.of(14, 0), LocalTime.of(15, 0))
        );
        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                schedules,
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                3,
                40000,
                "초급",
                "강남역",
                "010-9999-9999",
                "메모"
        );

        // when
        Integer weekCount = dto.getWeekCount();

        // then
        assertThat(weekCount).isEqualTo(3);
    }

    @Test
    @DisplayName("getWeekCount() - 요일이 1개면 주 1회")
    void getWeekCount_SingleDay_ReturnsOne() {
        // given
        List<ContractScheduleDto> schedules = List.of(
                new ContractScheduleDto(2, "화", LocalTime.of(14, 0), LocalTime.of(15, 0))
        );
        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                schedules,
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.FIRSTCOME,
                1,
                50000,
                null,
                null,
                null,
                null
        );

        // when
        Integer weekCount = dto.getWeekCount();

        // then
        assertThat(weekCount).isEqualTo(1);
    }

    @Test
    @DisplayName("필수 필드가 모두 있는 정규 레슨 DTO")
    void validRegularLessonDto() {
        // given
        List<ContractScheduleDto> schedules = List.of(
                new ContractScheduleDto(1, "월", LocalTime.of(15, 0), LocalTime.of(16, 0)),
                new ContractScheduleDto(3, "수", LocalTime.of(15, 0), LocalTime.of(16, 0))
        );
        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "김철수",
                "010-2222-3333",
                schedules,
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                2,
                35000,
                "중급",
                "홍대입구역",
                "010-8888-8888",
                "정규 레슨 메모"
        );

        // then
        assertThat(dto.studentName()).isEqualTo("김철수");
        assertThat(dto.phone()).isEqualTo("010-2222-3333");
        assertThat(dto.contractType()).isEqualTo(ContractType.REGULAR);
        assertThat(dto.unitPrice()).isEqualTo(35000);
        assertThat(dto.getWeekCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("선택 필드가 null인 선착순 레슨 DTO")
    void validFirstComeLessonDto_WithNullOptionalFields() {
        // given
        List<ContractScheduleDto> schedules = List.of(
                new ContractScheduleDto(5, "금", LocalTime.of(16, 0), LocalTime.of(17, 0))
        );
        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "이영희",
                "010-3333-4444",
                schedules,
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 10),
                ReservationStatus.REQUESTED,
                ContractType.FIRSTCOME,
                1,
                150000,
                null,
                null,
                null,
                null
        );

        // then
        assertThat(dto.studentName()).isEqualTo("이영희");
        assertThat(dto.contractType()).isEqualTo(ContractType.FIRSTCOME);
        assertThat(dto.level()).isNull();
        assertThat(dto.place()).isNull();
        assertThat(dto.emergencyContact()).isNull();
        assertThat(dto.getWeekCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("체험 레슨 DTO")
    void validTrialLessonDto() {
        // given
        List<ContractScheduleDto> schedules = List.of(
                new ContractScheduleDto(6, "토", LocalTime.of(10, 0), LocalTime.of(11, 0))
        );
        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "박민수",
                "010-4444-5555",
                schedules,
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 11),
                ReservationStatus.REQUESTED,
                ContractType.TRIAL,
                1,
                30000,
                "입문",
                "강남",
                null,
                "체험 레슨"
        );

        // then
        assertThat(dto.contractType()).isEqualTo(ContractType.TRIAL);
        assertThat(dto.unitPrice()).isEqualTo(30000);
        assertThat(dto.getWeekCount()).isEqualTo(1);
    }
}
