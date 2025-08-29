package com.tunit.domain.tutor.service;

import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.common.util.ExcelParser;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;
    private final LessonReservationRepository lessonReservationRepository;

    public TutorProfileResponseDto findTutorProfileInfo(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByTutorProfileNo(tutorProfileNo)
                .orElseThrow(() -> new IllegalArgumentException("Tutor profile not found"));

        return TutorProfileResponseDto.from(tutorProfile,
                tutorAvailableTimeService.findByTutorProfileNo(tutorProfileNo),
                tutorHolidayService.findByTutorProfileNo(tutorProfileNo));
    }

    @Transactional
    public Long save(Long userNo, TutorProfileSaveDto tutorProfileSaveDto) {
        TutorProfile tutorProfile = TutorProfile.saveFrom(userNo, tutorProfileSaveDto);

        TutorProfile save = tutorProfileRepository.save(tutorProfile);

        if (!tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList().isEmpty()) {
            tutorAvailableTimeService.saveAvailableTime(save.getTutorProfileNo(), tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList());
        }

        return save.getTutorProfileNo();
    }

    public TutorProfile findByUserNo(@NonNull Long userNo) {
        return tutorProfileRepository.findByUserNo(userNo);
    }

    public boolean existsLessons(@NonNull Long tutorProfileNo) {
        return lessonReservationRepository.existsByTutorProfileNo(tutorProfileNo);
    }

    public boolean existsLessonsByUserNo(Long userNo) {
        return lessonReservationRepository.existsByUserNo(userNo);
    }

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
}
