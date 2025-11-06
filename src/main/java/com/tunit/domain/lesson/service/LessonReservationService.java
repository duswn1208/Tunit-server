package com.tunit.domain.lesson.service;

import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.lesson.validate.LessonValidate;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.repository.TutorLessonsRepository;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 레슨 예약 생성 및 변경 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LessonReservationService {
    private final LessonReservationRepository lessonReservationRepository;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final TutorLessonsRepository tutorLessonsRepository;
    private final LessonValidate lessonValidate;

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
        lessonValidate.validateTutorAvailability(dto.tutorProfileNo(), dto.lessonDate(), dto.startTime(), endTime);

        // 레슨 예약정보 저장
        LessonReservation reservation = LessonReservation.fromReserveLesson(dto, dto.tutorProfileNo(), tutorLessons.getLessonSubCategory(), student.getUserNo(), endTime);
        lessonReservationRepository.save(reservation);
        log.info("레슨 예약 완료. studentNo: {}, tutorProfileNo: {}, date: {}", student.getUserNo(), dto.tutorProfileNo(), dto.lessonDate());
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
            lessonValidate.validateTutorAvailability(contract.getTutorProfileNo(), startTime.toLocalDate(), startTime.toLocalTime(), endTime);

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
    public void rescheduleLesson(Long lessonReservationNo, LocalDate lessonDate, LocalTime startTime, LocalTime endTime, String memo) {
        LessonReservation reservation = lessonReservationRepository.findById(lessonReservationNo)
                .orElseThrow(() -> new LessonNotFoundException("해당 레슨 예약을 찾을 수 없습니다."));

        // 튜터 일정 검증 (영업시간, 예외일정, 튜터의 다른 레슨)
        lessonValidate.validateTutorAvailability(reservation.getTutorProfileNo(), lessonDate, startTime, endTime);

        // 학생의 해당 시간대 다른 레슨 중복 체크 (자기 자신 제외)
        boolean studentHasConflict = lessonReservationRepository.existsByStudentNoAndDateAndStartTimeAndEndTimeAndStatusInAndLessonReservationNoNot(
                reservation.getStudentNo(), lessonDate, startTime, endTime, ReservationStatus.VALID_LESSON_STATUSES, lessonReservationNo);
        if (studentHasConflict) {
            throw new IllegalArgumentException("학생의 해당 시간대에 이미 예약된 레슨이 있습니다.");
        }

        reservation.reschedule(lessonDate, startTime, endTime, memo);
        log.info("레슨 일정 변경 완료. lessonReservationNo: {}, newDate: {}, newStartTime: {}, newEndTime: {}",
                lessonReservationNo, lessonDate, startTime, endTime);
    }

    @Transactional
    public void changeLesson(Long userNo, LessonReserveSaveDto lessonReserveSaveDto) {
        // TODO: 구현 필요
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
}

