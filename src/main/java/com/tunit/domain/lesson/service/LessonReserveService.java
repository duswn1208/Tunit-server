package com.tunit.domain.lesson.service;

import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.dto.LessonResponsDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.repository.TutorAvailExceptionRepository;
import com.tunit.domain.tutor.repository.TutorAvailableTimeRepository;
import com.tunit.domain.tutor.repository.TutorLessonsRepository;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonReserveService {
    private static final Logger log = LoggerFactory.getLogger(LessonReserveService.class);

    private final LessonReservationRepository lessonReservationRepository;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final TutorAvailableTimeRepository tutorAvailableTimeRepository;
    private final TutorAvailExceptionRepository tutorAvailExceptionRepository;
    private final TutorLessonsRepository tutorLessonsRepository;

    @Transactional
    public void createLesson(Long tutorProfileNo, LessonReserveSaveDto lessonReserveSaveDto) {
        try {
            UserMain student = userService.getOrCreateWaitingStudent(lessonReserveSaveDto.studentName(), lessonReserveSaveDto.phone());

            TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutorProfileInfoByTutorProfileNo(tutorProfileNo);

            LessonReservation lessonReservation = LessonReservation.fromLessonSaveDto(tutorProfileInfo, student, lessonReserveSaveDto);
            if (lessonReservationRepository.existsByTutorProfileNoAndDateAndStartTimeAndEndTimeAndStatusIn(
                    tutorProfileNo,
                    lessonReservation.getDate(),
                    lessonReservation.getStartTime(),
                    lessonReservation.getEndTime(),
                    List.of(ReservationStatus.ACTIVE, ReservationStatus.TRIAL_ACTIVE, ReservationStatus.REQUESTED, ReservationStatus.COMPLETED)
            )) {
                throw new IllegalStateException("해당 시간대에 이미 예약된 레슨이 있습니다.");
            }

            lessonReservationRepository.save(lessonReservation);
        } catch (Exception e) {
            log.error("레슨 예약 저장 중 에러 발생", e);
            throw e;
        }
    }

    @Transactional
    public void reserveLesson(Long userNo, LessonReserveSaveDto dto) {
        // 예약 요청자(학생) 정보 조회
        UserMain student = userService.findByUserNo(userNo);

        // 튜터 프로필 존재 여부 확인
        TutorProfileResponseDto tutorProfile = tutorProfileService.findTutorProfileInfoByTutorProfileNo(dto.tutorProfileNo());

        // 튜터레슨 존재 여부 확인
        TutorLessons tutorLessons = tutorLessonsRepository.findById(dto.tutorLessonNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 레슨 정보가 없습니다. tutorLessonNo: " + dto.tutorLessonNo()));

        // 선택한 날짜가 튜터의 영업일인지 확인 (예외일정, 휴무일 체크)
        LocalTime endTime = dto.startTime().plusMinutes(tutorProfile.durationMin());
        validateTutorAvailability(dto.tutorProfileNo(), dto.lessonDate(), dto.startTime(), endTime);

        // 레슨 예약정보 저장
        LessonReservation reservation = LessonReservation.fromReserveLesson(dto, dto.tutorProfileNo(), tutorLessons.getLessonSubCategory(), student.getUserNo(), endTime);
        lessonReservationRepository.save(reservation);
        log.info("레슨 예약 완료. studentNo: {}, tutorProfileNo: {}, date: {}", student.getUserNo(), dto.tutorProfileNo(), dto.lessonDate());
    }

    /**
     * 계약 생성 시 여러 개의 대기 상태 레슨을 배치로 생성
     *
     */
    @Transactional
    public void reserveLessonsBatch(StudentTutorContract contract, List<LocalDateTime> lessonDtList) {
        if (lessonDtList == null || lessonDtList.isEmpty()) {
            return;
        }

        // 1. 학생 정보 1번만 조회
        UserMain student = userService.findByUserNo(contract.getStudentNo());

        // 2. 튜터 프로필 1번만 조회
        TutorProfileResponseDto tutorProfile = tutorProfileService.findTutorProfileInfoByTutorProfileNo(contract.getTutorProfileNo());

        // 3. 튜터 레슨 1번만 조회
        if (!tutorLessonsRepository.existsByLessonSubCategory(contract.getLessonSubCategory())) {
            throw new LessonNotFoundException();
        }

        // 4. 레슨 예약 엔티티 생성
        List<LessonReservation> reservations = new ArrayList<>();
        for (LocalDateTime startTime : lessonDtList) {
            // 종료 시간 계산
            LocalTime endTime = LocalTime.from(startTime.plusMinutes(tutorProfile.durationMin()));

            // 튜터 가용성 검증
            validateTutorAvailability(contract.getTutorProfileNo(), startTime.toLocalDate(), startTime.toLocalTime(), endTime);

            // 레슨 예약 엔티티 생성
            LessonReservation reservation = LessonReservation.fromContract(contract, startTime.toLocalDate(), startTime.toLocalTime(), endTime);
            reservations.add(reservation);
        }

        // 5. 한 번에 배치 저장
        lessonReservationRepository.saveAll(reservations);
        log.info("배치 레슨 예약 완료. studentNo: {}, tutorProfileNo: {}, 레슨 수: {}",
                student.getUserNo(), tutorProfile.tutorProfileNo(), reservations.size());
    }

    @Transactional
    public void changeLesson(Long userNo, LessonReserveSaveDto lessonReserveSaveDto) {
    }

    private void validateTutorAvailability(Long tutorProfileNo, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 1. 해당 요일이 튜터의 영업일이고 해당 시간이 영업시간인지 확인
        int dayOfWeek = date.getDayOfWeek().getValue();
        boolean isAvailableTime = tutorAvailableTimeRepository.existsByTutorProfileNoAndDayOfWeekNumAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                tutorProfileNo, dayOfWeek, startTime, endTime);
        if (!isAvailableTime) {
            throw new IllegalArgumentException("튜터의 영업시간이 아닙니다.");
        }

        // 2. 예외일정이 있는지 확인
        boolean hasException = tutorAvailExceptionRepository.existsByTutorProfileNoAndDateAndTypeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                tutorProfileNo, date, TutorLessonOpenType.BLOCK, startTime, endTime);
        if (hasException) {
            throw new IllegalArgumentException("튜터의 예외일정이 있는 시간입니다.");
        }

        // 3. 해당 시간에 다른 레슨이 있는지 확인
        boolean hasOverlappingLesson = lessonReservationRepository.existsByTutorProfileNoAndDateAndStartTimeAndEndTimeAndStatusIn(
                tutorProfileNo,
                date,
                startTime,
                endTime,
                ReservationStatus.VALID_LESSON_STATUSES
        );
        if (hasOverlappingLesson) {
            throw new IllegalArgumentException("이미 예약된 시간입니다.");
        }
    }

    public void saveLessonFromFixedLessonFromExcel(FixedLessonReservation fixedLessonReservation) {
        LessonReservation lessonReservation = LessonReservation.fromFixedLessonExcelUpload(fixedLessonReservation);
        saveMonthlyLessonReservations(lessonReservation);
    }

    public void saveLessonFromFixedLessonFromWeb(FixedLessonReservation fixedLessonReservation) {
        LessonReservation lessonReservation = LessonReservation.fromFixedLessonFromWeb(fixedLessonReservation);
        saveMonthlyLessonReservations(lessonReservation);
    }

    private void saveMonthlyLessonReservations(LessonReservation lessonReservation) {
        LocalDate startDate = lessonReservation.getDate();
        Integer dayOfWeekNum = lessonReservation.getDayOfWeekNum();
        List<LessonReservation> reservations = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            // 요일이 같으려면 7일씩 더함
            LocalDate targetDate = startDate.plusDays(i * 7);
            if (targetDate.getDayOfWeek().getValue() == dayOfWeekNum) {
                LessonReservation newLesson = lessonReservation.copyWithDate(targetDate);
                tutorProfileService.checkBusinessAndHolidays(lessonReservation);
                reservations.add(newLesson);
            }
        }
        lessonReservationRepository.saveAll(reservations);
    }

    public boolean existsLessons(@NonNull Long tutorProfileNo) {
        return lessonReservationRepository.existsByTutorProfileNo(tutorProfileNo);
    }

    public LessonFindSummaryDto getLessonSummary(LessonFindRequestDto lessonFindRequestDto) {
        List<LessonResponsDto> lessonList = lessonReservationRepository.findByTutorProfileNoAndDateBetweenWithUser(
                lessonFindRequestDto.getTutorProfileNo(),
                lessonFindRequestDto.getStartDate(),
                lessonFindRequestDto.getEndDate()
        );

        return LessonFindSummaryDto.from(lessonList);
    }

    public void deleteLesson(Long lessonNo) {
        if (!lessonReservationRepository.existsById(lessonNo)) {
            throw new LessonNotFoundException("Lesson not found with lessonNo: " + lessonNo);
        }
        lessonReservationRepository.deleteById(lessonNo);
    }

    @Transactional
    public void changeLessonStatus(Long lessonNo, ReservationStatus status) {
        LessonReservation lessonReservation = findByLessonReservationNo(lessonNo);
        lessonReservation.changeStatus(lessonReservation.getStatus(), status);
    }

    @Transactional
    public void changeLessonStatusByContractNo(Long contractNo, ReservationStatus status) {
        List<LessonReservation> lessonReservations = this.findByContractNo(contractNo);
        for (LessonReservation lessonReservation : lessonReservations) {
            lessonReservation.changeStatus(lessonReservation.getStatus(), status);
        }
    }

    @Transactional
    public void cancel(Long userNo, Long lessonReservationNo, ReservationStatus status) {
        LessonReservation lessonReservation = findByLessonReservationNo(lessonReservationNo);
        if (!lessonReservation.getStudentNo().equals(userNo)) {
            throw new LessonNotFoundException("Lesson reservation not found for userNo: " + userNo + " and lessonReservationNo: " + lessonReservationNo);
        }
        lessonReservation.changeStatus(lessonReservation.getStatus(), status);
    }

    public List<LessonReservation> findByContractNo(Long contractNo) {
        return Collections.singletonList(lessonReservationRepository.findByContractNo(contractNo)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with contractNo: " + contractNo)));
    }

    public LessonReservation findByLessonReservationNo(Long lessonReservationNo) {
        return lessonReservationRepository.findById(lessonReservationNo)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with lessonNo: " + lessonReservationNo));
    }
}
