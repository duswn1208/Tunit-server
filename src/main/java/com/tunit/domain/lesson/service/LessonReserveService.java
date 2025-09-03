package com.tunit.domain.lesson.service;

import com.tunit.common.util.ExcelParser;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindResponseDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonReserveService {

    private final LessonReservationRepository lessonReservationRepository;

    public void saveLessonFromFixedLesson(FixedLessonReservation fixedLessonReservation) {
        LessonReservation lessonReservation = LessonReservation.fromFixedLesson(fixedLessonReservation);
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

    public List<LessonFindResponseDto> getLessons(LessonFindRequestDto lessonFindRequestDto) {
        return lessonReservationRepository.findByTutorProfileNoAndDateBetweenWithUser(
                lessonFindRequestDto.getTutorProfileNo(),
                lessonFindRequestDto.getStartDate(),
                lessonFindRequestDto.getEndDate()
        );
    }
}
