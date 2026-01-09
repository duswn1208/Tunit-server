package com.tunit.domain.contract.service;

import com.tunit.common.util.KoreanDayOfWeekUtil;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractExcelSaveDto;
import com.tunit.domain.contract.dto.ContractExcelUploadResultDto;
import com.tunit.domain.contract.dto.ContractScheduleDto;
import com.tunit.domain.contract.entity.ContractSchedule;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.repository.ContractScheduleRepository;
import com.tunit.domain.contract.util.ContractExcelRowParser;
import com.tunit.domain.lesson.define.LessonUploadFailReason;
import com.tunit.domain.lesson.dto.*;
import com.tunit.domain.lesson.exception.LessonDuplicationException;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.service.LessonReserveProcessorService;
import com.tunit.domain.lesson.service.LessonReserveService;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractExcelService {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[016789]-?\\d{3,4}-?\\d{4}$");

    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final ContractExcelRowParser contractExcelRowParser;
    private final LessonReserveProcessorService lessonReserveService;
    private final ContractQueryService contractQueryService;
    private final ContractScheduleRepository contractScheduleRepository;

    /**
     * 계약 정보를 포함한 엑셀 업로드 (신규)
     */
    @Transactional
    public ContractExcelUploadResultDto uploadExcelWithContract(@NonNull Long tutorProfileNo, MultipartFile file) {
        List<ContractExcelSaveDto> rows = contractExcelRowParser.parseWithContract(file);
        List<FailedStudentDto> failList = new ArrayList<>();

        Integer durationMin = tutorProfileService.findDurationMinByTutorProfileNo(tutorProfileNo);

        for (ContractExcelSaveDto dto : rows) {
            if (!isValidDtoWithContract(dto)) {
                addFailedStudentWithContract(failList, dto, LessonUploadFailReason.REQUIRED_FIELD_MISSING);
                continue;
            }

            UserMain student;
            try {
                student = userService.getOrCreateWaitingStudent(dto.studentName(), dto.phone());
            } catch (Exception e) {
                addFailedStudentWithContract(failList, dto, LessonUploadFailReason.USER_SIGNUP_FAIL);
                continue;
            }
            if (student == null) continue;

            try {
                // 1. 계약 생성
                StudentTutorContract contract = createContractFromDto(tutorProfileNo, student.getUserNo(), dto, durationMin);

                // 2. 계약 스케줄 저장 (요일별 시간 정보)
                saveContractSchedules(contract, dto.schedules());

                // 3. 레슨 예약 생성
                switch (contract.getContractType()) {
                    case REGULAR -> {
                        // 정규 레슨: 각 스케줄별로 레슨 생성
                        for (ContractScheduleDto schedule : dto.schedules()) {
                            List<LocalDateTime> lessonDtList = generateLessonDateTimes(
                                    dto.firstLessonDate(),
                                    schedule,
                                    contract.getLessonCount(),
                                    dto.getWeekCount()
                            );
                            lessonReserveService.reserveLessonsBatch(contract, lessonDtList);
                        }
                    }
                    case FIRSTCOME, TRIAL -> {
                        // 선착순/체험 레슨: 첫 번째 스케줄로 1회 생성
                        ContractScheduleDto firstSchedule = dto.schedules().get(0);
                        LocalDateTime lessonDateTime = LocalDateTime.of(dto.firstLessonDate(), firstSchedule.startTime());
                        lessonReserveService.reserveLessonsBatch(contract, List.of(lessonDateTime));
                    }
                }

            } catch (LessonDuplicationException e) {
                addFailedStudentWithContract(failList, dto, LessonUploadFailReason.LESSON_DUPLICATION);
            } catch (LessonNotFoundException e) {
                addFailedStudentWithContract(failList, dto, LessonUploadFailReason.LESSON_NOT_FOUND);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                addFailedStudentWithContract(failList, dto, LessonUploadFailReason.UNKNOWN_ERROR);
            }
        }
        return new ContractExcelUploadResultDto(failList.size(), failList);
    }

    /**
     * 계약 스케줄 저장
     */
    private void saveContractSchedules(StudentTutorContract contract, List<ContractScheduleDto> scheduleDtos) {
        List<ContractSchedule> schedules = scheduleDtos.stream()
                .map(dto -> ContractSchedule.of(
                        contract,
                        KoreanDayOfWeekUtil.getDayOfWeekNum(dto.dayOfWeek()),
                        dto.startTime(),
                        dto.endTime()
                ))
                .toList();

        contractScheduleRepository.saveAll(schedules);
    }

    /**
     * 정규 레슨의 날짜/시간 리스트 생성
     *
     * @param firstLessonDate 첫 레슨 날짜
     * @param schedule        스케줄 (요일, 시작/종료 시간)
     * @param totalLessonCount 총 레슨 횟수
     * @param weekCount       주 횟수
     * @return 레슨 날짜/시간 리스트
     */
    private List<LocalDateTime> generateLessonDateTimes(
            LocalDate firstLessonDate,
            ContractScheduleDto schedule,
            Integer totalLessonCount,
            Integer weekCount) {

        List<LocalDateTime> lessonDtList = new ArrayList<>();
        int lessonsPerSchedule = totalLessonCount / weekCount; // 이 요일의 총 레슨 횟수

        LocalDate currentDate = firstLessonDate;
        int targetDayOfWeek = KoreanDayOfWeekUtil.getDayOfWeekNum(schedule.dayOfWeek());

        // 첫 레슨 날짜를 해당 요일로 조정
        while (currentDate.getDayOfWeek().getValue() != targetDayOfWeek) {
            currentDate = currentDate.plusDays(1);
        }

        // 매주 같은 요일에 레슨 생성
        for (int i = 0; i < lessonsPerSchedule; i++) {
            lessonDtList.add(LocalDateTime.of(currentDate, schedule.startTime()));
            currentDate = currentDate.plusWeeks(1);
        }

        return lessonDtList;
    }

    /**
     * DTO로부터 계약 생성
     */
    private StudentTutorContract createContractFromDto(Long tutorProfileNo, Long studentNo, ContractExcelSaveDto dto, Integer durationMin) {
        // 첫 번째 스케줄의 날짜/시간만 가져옴 (레슨 생성용)
        ContractScheduleDto firstSchedule = dto.schedules().get(0);
        LocalDateTime firstLessonDateTime = LocalDateTime.of(dto.firstLessonDate(), firstSchedule.startTime());

        ContractCreateRequestDto contractDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(studentNo)
                .contractType(dto.contractType())
                .lessonCategory(dto.lesson())
                .weekCount(dto.getWeekCount())
                .lessonCount(dto.getWeekCount() * 4)
                .totalPrice(dto.unitPrice() * 4 * dto.getWeekCount())
                .level(dto.level())
                .place(dto.place())
                .emergencyContact(dto.emergencyContact())
                .memo(dto.memo())
                .source(ContractSource.TUTOR_OFFER)
                .lessonDtList(List.of(firstLessonDateTime)) // 시작일 설정용
                .build();

        return contractQueryService.createContract(contractDto, durationMin);
    }

    private void addFailedStudentWithContract(List<FailedStudentDto> failList, ContractExcelSaveDto dto, LessonUploadFailReason reason) {
        log.info("Failed to process student: {}, Reason: {}", dto, reason);
        failList.add(new FailedStudentDto(
                dto.studentName(),
                dto.phone(),
                dto.memo(),
                reason.getMessage()
        ));
    }

    private boolean isValidDtoWithContract(ContractExcelSaveDto dto) {
        if (dto.studentName() == null || dto.studentName().isBlank()) return false;
        if (dto.phone() == null || !PHONE_PATTERN.matcher(dto.phone()).matches()) return false;
        if (dto.schedules() == null || dto.schedules().isEmpty()) return false;
        if (dto.firstLessonDate() == null) return false;
        if (dto.contractType() == null) return false;
        if (dto.weekCount() == null) return false;
        if (dto.unitPrice() == null) return false;
        return true;
    }
}
