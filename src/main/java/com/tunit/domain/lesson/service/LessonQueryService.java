package com.tunit.domain.lesson.service;

import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.entity.TrialContractCandidate;
import com.tunit.domain.contract.repository.TrialContractCandidateRepository;
import com.tunit.domain.contract.service.ContractQueryService;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.dto.LessonResponseDto;
import com.tunit.domain.lesson.dto.LessonScheduleStatusDto;
import com.tunit.domain.lesson.dto.TrialCandidateLessonDto;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorAvailExceptionResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailableTimeResponseDto;
import com.tunit.domain.tutor.service.TutorAvailableTimeService;
import com.tunit.domain.tutor.service.TutorHolidayService;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 레슨 조회 전용 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonQueryService {
    private final LessonReservationRepository lessonReservationRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;
    private final ContractQueryService contractQueryService;
    private final TrialContractCandidateRepository trialCandidateRepository;
    private final UserService userService;

    public LessonFindSummaryDto getLessonSummary(LessonFindRequestDto lessonFindRequestDto) {
        List<LessonResponseDto> lessonList = lessonReservationRepository.findByTutorProfileNoAndDateBetweenWithUser(
                lessonFindRequestDto.getTutorProfileNo(),
                lessonFindRequestDto.getStartDate(),
                lessonFindRequestDto.getEndDate()
        );

        List<TrialCandidateLessonDto> trialCandidates = findPendingTrialFirstCandidates(
                lessonFindRequestDto.getTutorProfileNo(),
                lessonFindRequestDto.getStartDate(),
                lessonFindRequestDto.getEndDate()
        );

        return LessonFindSummaryDto.from(lessonList, trialCandidates);
    }

    /**
     * 미확정 체험 계약들의 1순위 후보 시간 중, 조회 기간에 속하는 것만 잠정 표시용으로 변환.
     * (lesson_reservation 이 아직 없는 단계라 캘린더에 점선으로만 노출)
     */
    private List<TrialCandidateLessonDto> findPendingTrialFirstCandidates(Long tutorProfileNo, LocalDate startDate, LocalDate endDate) {
        return contractQueryService.findPendingTrialContractsByTutor(tutorProfileNo).stream()
                .map(contract -> toFirstCandidate(contract, startDate, endDate))
                .filter(Objects::nonNull)
                .toList();
    }

    private TrialCandidateLessonDto toFirstCandidate(StudentTutorContract contract, LocalDate startDate, LocalDate endDate) {
        List<TrialContractCandidate> candidates = trialCandidateRepository
                .findByContract_ContractNoOrderByPriority(contract.getContractNo());
        if (candidates.isEmpty()) {
            return null;
        }

        TrialContractCandidate first = candidates.get(0); // priority 1
        LocalDate date = first.getCandidateDate();
        if (date == null || date.isBefore(startDate) || date.isAfter(endDate)) {
            return null;
        }

        String studentName = userService.findByUserNo(contract.getStudentNo()).getName();
        return new TrialCandidateLessonDto(
                contract.getContractNo(),
                studentName,
                date,
                first.getCandidateStartTime(),
                first.getCandidateStartTime().plusHours(1),
                first.getPriority()
        );
    }

    public boolean existsLessons(@NonNull Long tutorProfileNo) {
        return lessonReservationRepository.existsByTutorProfileNo(tutorProfileNo);
    }

    public List<LessonReservation> findByContractNo(Long contractNo) {
        return lessonReservationRepository.findByContractNo(contractNo)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with contractNo: " + contractNo));
    }

    public List<LessonReservation> findByContractNoAndStatusIn(Long contractNo, List<ReservationStatus> statuses) {
        return lessonReservationRepository.findByContractNoAndStatusIn(contractNo, statuses);
    }

    public LessonReservation findByLessonReservationNo(Long lessonReservationNo) {
        return lessonReservationRepository.findById(lessonReservationNo)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with lessonNo: " + lessonReservationNo));
    }

    public LessonScheduleStatusDto getLessonScheduleInfo(LessonFindRequestDto lessonFindRequestDto) {

        List<TutorAvailableTimeResponseDto> availableTimes = tutorAvailableTimeService.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());
        List<TutorAvailExceptionResponseDto> holidayDates = tutorHolidayService.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());

        List<LessonReservation> lessonReservations = lessonReservationRepository.findByTutorProfileNoAndDateBetweenAndStatusIn(lessonFindRequestDto.getTutorProfileNo(), lessonFindRequestDto.getStartDate(), lessonFindRequestDto.getEndDate(), ReservationStatus.VALID_LESSON_STATUSES);

        return new LessonScheduleStatusDto(availableTimes, holidayDates, lessonReservations);
    }

}

