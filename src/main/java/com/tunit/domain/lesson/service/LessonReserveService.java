package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.dto.LessonResponsDto;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonReserveService {
    private static final Logger log = LoggerFactory.getLogger(LessonReserveService.class);

    private final LessonReservationRepository lessonReservationRepository;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;

    @Transactional
    public void reserveLesson(Long tutorProfileNo, LessonReserveSaveDto lessonReserveSaveDto) {
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

    public void saveLessonFromFixedLessonFromExcel(FixedLessonReservation fixedLessonReservation) {
        LessonReservation lessonReservation = LessonReservation.fromFixedLessonExcelUpload(fixedLessonReservation);
        lessonReservationRepository.save(lessonReservation);
    }

    public void saveLessonFromFixedLessonFromWeb(FixedLessonReservation fixedLessonReservation) {
        LessonReservation lessonReservation = LessonReservation.fromFixedLessonFromWeb(fixedLessonReservation);
        lessonReservationRepository.save(lessonReservation);
    }

    private LessonSubCategory getLessonSubCategoryByLabel(String label) {
        for (LessonSubCategory subCategory : LessonSubCategory.values()) {
            if (subCategory.getLabel().equals(label)) {
                return subCategory;
            }
        }
        return null;
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
        LessonReservation lessonReservation = lessonReservationRepository.findById(lessonNo)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with lessonNo: " + lessonNo));
        lessonReservation.changeStatus(lessonReservation.getStatus(), status);
    }

}
