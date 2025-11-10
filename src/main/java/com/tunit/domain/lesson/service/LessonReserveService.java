package com.tunit.domain.lesson.service;

import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.service.ContractQueryService;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.exception.LessonStatusException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.repository.TutorAvailExceptionRepository;
import com.tunit.domain.tutor.repository.TutorLessonsRepository;
import com.tunit.domain.tutor.service.TutorAvailableTimeService;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonReserveService {
    private static final Logger log = LoggerFactory.getLogger(LessonReserveService.class);

    private final LessonService lessonService;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final ContractQueryService contractQueryService;

    private final LessonReservationRepository lessonReservationRepository;
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
                    List.of(ReservationStatus.ACTIVE, ReservationStatus.REQUESTED, ReservationStatus.COMPLETED)
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
    public void reserveLesson(Long studentNo, LessonReserveSaveDto dto) {
        StudentTutorContract contract = contractQueryService.getContract(dto.contractNo());
        TutorProfileResponseDto tutor = tutorProfileService.findTutor(contract.getTutorProfileNo());

        LocalTime endTime = dto.startTime().plusMinutes(tutor.durationMin());
        validateTutorAvailability(tutor.tutorProfileNo(), dto.lessonDate(), dto.startTime(), endTime);

        // 레슨 예약정보 저장
        LessonReservation reservation = LessonReservation.fromReserveLesson(dto, tutor.tutorProfileNo(), contract.getLessonSubCategory(), studentNo, endTime);
        lessonReservationRepository.save(reservation);
        log.info("레슨 예약 완료. studentNo: {}, tutorProfileNo: {}, date: {}", studentNo, dto.tutorProfileNo(), dto.lessonDate());
    }

    /**
     * 계약 생성 시 여러 개의 대기 상태 레슨을 배치로 생성
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

    private void validateTutorAvailability(@NonNull Long tutorProfileNo, @NonNull LocalDate date, @NonNull LocalTime startTime, @NonNull LocalTime endTime) {
        // 1. 해당 요일이 튜터의 영업일이고 해당 시간이 영업시간인지 확인
        int dayOfWeek = date.getDayOfWeek().getValue();
        List<TutorAvailableTime> allByTutorProfileNo = tutorAvailableTimeService.findAvailableTimeByTutorProfileNo(tutorProfileNo);
        boolean isAvailableTime = tutorAvailableTimeService.existsByTutorProfileNoAndDayOfWeekNumAndRequestTimeBetweenStartTimeAndEndTime(
                tutorProfileNo, dayOfWeek, startTime);
        if (!isAvailableTime) {
            throw new IllegalArgumentException("튜터의 영업시간이 아닙니다.");
        }

        // 2. 예외일정이 있는지 확인
        boolean hasException = tutorAvailExceptionRepository.existsByTutorProfileNoAndDateAndTypeAndRequestTimeBetweenStartTimeAndEndTime(
                tutorProfileNo, date, TutorLessonOpenType.BLOCK, startTime);
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


    public void deleteLesson(Long lessonNo) {
        if (!lessonReservationRepository.existsById(lessonNo)) {
            throw new LessonNotFoundException("Lesson not found with lessonNo: " + lessonNo);
        }
        lessonReservationRepository.deleteById(lessonNo);
    }

    @Transactional
    public void changeLessonStatus(Long lessonNo, ReservationStatus status) {
        LessonReservation lessonReservation = lessonService.findByLessonReservationNo(lessonNo);
        lessonReservation.changeStatus(lessonReservation.getStatus(), status);
    }

    @Transactional
    public void changeLessonStatusByContractNo(Long contractNo, ReservationStatus status) {
        try {
            List<LessonReservation> lessonReservations = lessonService.findByContractNo(contractNo);
            for (LessonReservation lessonReservation : lessonReservations) {
                lessonReservation.changeStatus(lessonReservation.getStatus(), status);
            }
        } catch (LessonStatusException e) {
            log.error("레슨 상태 변경 중 에러 발생. contractNo: {}, status: {}", contractNo, status, e);
        }
    }

    @Transactional
    public void cancel(Long userNo, Long lessonReservationNo, ReservationStatus status) {
        LessonReservation lessonReservation = lessonService.findByLessonReservationNo(lessonReservationNo);
        if (!lessonReservation.getStudentNo().equals(userNo)) {
            throw new LessonNotFoundException("Lesson reservation not found for userNo: " + userNo + " and lessonReservationNo: " + lessonReservationNo);
        }
        lessonReservation.changeStatus(lessonReservation.getStatus(), status);
    }

    @Transactional
    public void reschedule(Long userNo, Long lessonReservationNo, LessonReserveSaveDto dto) {
        LessonReservation byLessonReservationNo = lessonService.findByLessonReservationNo(lessonReservationNo);

        validateTutorAvailability(byLessonReservationNo.getTutorProfileNo(), dto.lessonDate(), dto.startTime(), dto.startTime().plusMinutes(1));

        byLessonReservationNo.updateLessonInfo(dto.lessonDate(), dto.startTime(), dto.startTime().plusHours(1));
        log.info("레슨 예약 변경 완료. userNo: {}, lessonReservationNo: {}, newDate: {}", userNo, lessonReservationNo, dto.lessonDate());
    }
}
