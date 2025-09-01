package com.tunit.domain.lesson.service;

import com.tunit.common.util.ExcelParser;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class LessonReserveService {

    private final LessonReservationRepository lessonReservationRepository;

    @Transactional
    public int uploadLessonsFromExcel(Long tutorProfileNo, MultipartFile file) {
        var reservations = ExcelParser.parse(file, row -> {
            String dateStr = ExcelParser.getCellString(row.getCell(0));
            String startTimeStr = ExcelParser.getCellString(row.getCell(1));
            String endTimeStr = ExcelParser.getCellString(row.getCell(2));
            String userNoStr = ExcelParser.getCellString(row.getCell(3));
            String statusStr = ExcelParser.getCellString(row.getCell(4));
            if (dateStr.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty() || userNoStr.isEmpty()) return null;
            try {
                LocalDate date = LocalDate.parse(dateStr);
                LocalTime startTime = LocalTime.parse(startTimeStr);
                LocalTime endTime = LocalTime.parse(endTimeStr);
                Long userNo = Long.parseLong(userNoStr);
                ReservationStatus status = statusStr.isEmpty() ? ReservationStatus.ACTIVE : ReservationStatus.valueOf(statusStr);
                return LessonReservation.excelUpload(tutorProfileNo, userNo, date, startTime, endTime, status);
            } catch (Exception e) {
                return null;
            }
        });
        lessonReservationRepository.saveAll(reservations);
        return reservations.size();
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
}
