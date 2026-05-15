package com.tunit.domain.lesson.service;

import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.repository.StudentTutorContractRepository;
import com.tunit.domain.contract.service.ContractQueryService;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonDuplicationException;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.lesson.validate.LessonValidate;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.repository.TutorLessonsRepository;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonReserveProcessorService {
    private final UserService userService;
    private final TutorLessonsRepository tutorLessonsRepository;
    private final ContractQueryService contractQueryService;
    private final TutorProfileService tutorProfileService;
    private final LessonValidate lessonValidate;
    private final LessonReservationRepository lessonReservationRepository;
    private final LessonManagementService lessonManagementService;
    private final StudentTutorContractRepository contractRepository;

    @Transactional
    public void processCreate(Long studentNo, LessonReserveSaveDto dto) {
        StudentTutorContract contract = contractQueryService.getContract(dto.contractNo());
        TutorProfileResponseDto tutor = tutorProfileService.findTutor(contract.getTutorProfileNo());
        LocalTime endTime = dto.startTime().plusMinutes(tutor.durationMin());
        lessonValidate.validateTutorAvailability(studentNo, tutor.tutorProfileNo(), dto.lessonDate(), dto.startTime(), endTime);
        LessonReservation reservation = LessonReservation.fromReserveLesson(dto, tutor.tutorProfileNo(), contract.getLessonSubCategory(), studentNo, endTime);
        try {
            // DB EXCLUDE 제약(no_overlap_lesson_reservation)으로 동시성 충돌 최종 차단
            lessonReservationRepository.saveAndFlush(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new LessonDuplicationException();
        }
        log.info("레슨 예약 완료. studentNo: {}, tutorProfileNo: {}, date: {}", studentNo, dto.tutorProfileNo(), dto.lessonDate());
    }

    @Transactional
    public void processReschedule(Long userNo, Long lessonReservationNo, LessonReserveSaveDto dto) {
        try {
            lessonManagementService.reschedule(userNo, lessonReservationNo, dto);
        } catch (DataIntegrityViolationException e) {
            throw new LessonDuplicationException();
        }
        log.info("레슨 예약 변경 완료. userNo: {}, lessonReservationNo: {}", userNo, lessonReservationNo);
    }

    @Transactional
    public void processCancel(Long userNo, Long lessonReservationNo) {
        lessonManagementService.cancel(userNo, lessonReservationNo, com.tunit.domain.lesson.define.ReservationStatus.CANCELED);
        log.info("레슨 예약 취소 완료. userNo: {}, lessonReservationNo: {}", userNo, lessonReservationNo);
    }

    @Transactional
    public void processTutorCreate(Long tutorProfileNo, LessonReserveSaveDto dto) {
        UserMain student = userService.getOrCreateWaitingStudent(dto.studentName(), dto.phone());
        TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutor(tutorProfileNo);

        // 기존 계약 조회, 없으면 직접 등록용 계약 생성
        Long contractNo = contractRepository
                .findByStudentNoAndTutorProfileNo(student.getUserNo(), tutorProfileNo)
                .stream()
                .filter(c -> c.getContractStatus() != ContractStatus.CANCELLED
                        && c.getContractStatus() != ContractStatus.END)
                .findFirst()
                .map(StudentTutorContract::getContractNo)
                .orElseGet(() -> {
                    StudentTutorContract newContract = StudentTutorContract.builder()
                            .tutorProfileNo(tutorProfileNo)
                            .studentNo(student.getUserNo())
                            .contractStatus(ContractStatus.ACTIVE)
                            .contractType(ContractType.REGULAR)
                            .lessonSubCategory(dto.lesson())
                            .lessonName(ContractCreateRequestDto.generateLessonName(dto.lesson(), ContractType.REGULAR, dto.weeklyCount(), null))
                            .source(ContractSource.TUTOR_OFFER)
                            .totalPrice(dto.price())
                            .weekCount(dto.weeklyCount())
                            .emergencyContact(dto.phone())
                            .build();
                    return contractRepository.save(newContract).getContractNo();
                });

        LessonReservation lessonReservation = LessonReservation.fromLessonSaveDto(tutorProfileInfo, student, dto, contractNo);
        try {
            // DB EXCLUDE 제약(no_overlap_lesson_reservation)으로 동시성 충돌 최종 차단
            lessonReservationRepository.saveAndFlush(lessonReservation);
        } catch (DataIntegrityViolationException e) {
            throw new LessonDuplicationException();
        }
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
        TutorProfileResponseDto tutorProfile = tutorProfileService.findTutor(contract.getTutorProfileNo());

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
            lessonValidate.validateTutorAvailability(student.getUserNo(), contract.getTutorProfileNo(), startTime.toLocalDate(), startTime.toLocalTime(), endTime);

            // 레슨 예약 엔티티 생성
            LessonReservation reservation = LessonReservation.fromContract(contract, startTime.toLocalDate(), startTime.toLocalTime(), endTime);
            reservations.add(reservation);
        }

        // 5. 한 번에 배치 저장
        lessonReservationRepository.saveAll(reservations);
        log.info("배치 레슨 예약 완료. studentNo: {}, tutorProfileNo: {}, 레슨 수: {}",
                student.getUserNo(), tutorProfile.tutorProfileNo(), reservations.size());
    }


}
