package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.dto.LessonResponsDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonReserveService {

    private final LessonReservationRepository lessonReservationRepository;

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
