package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.LessonUploadFailReason;
import com.tunit.domain.lesson.dto.FailedStudentDto;
import com.tunit.domain.lesson.dto.FixedLessonExcelDto;
import com.tunit.domain.lesson.dto.FixedLessonUploadResultDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.FixedLessonReservationRepository;
import com.tunit.domain.lesson.util.FixedLessonExcelRowParser;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.tunit.common.util.KoreanDayOfWeekUtil.DAY_OF_WEEK_SET;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedLessonExcelService {
    private final FixedLessonReservationRepository fixedLessonReservationRepository;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final FixedLessonExcelRowParser fixedLessonExcelRowParser;
    private final LessonReserveService lessonReserveService;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[016789]-?\\d{3,4}-?\\d{4}$");

    @Transactional
    public FixedLessonUploadResultDto uploadExcel(@NonNull Long tutorProfileNo, MultipartFile file) {
        List<FixedLessonExcelDto> rows = fixedLessonExcelRowParser.parse(file);
        List<FailedStudentDto> failList = new ArrayList<>();
        for (FixedLessonExcelDto dto : rows) {
            if (!isValidDto(dto)) {
                addFailedStudent(failList, dto, LessonUploadFailReason.REQUIRED_FIELD_MISSING);
                continue;
            }

            UserMain student = userService.findByNameAndPhone(dto.getName(), dto.getPhone());
            if (student == null) {
                try {
                    student = userService.saveWaitingStudent(dto.getName(), dto.getPhone());
                } catch (Exception e) {
                    addFailedStudent(failList, dto, LessonUploadFailReason.USER_SIGNUP_FAIL);
                    continue;
                }
            }

            try {
                TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutorProfileInfo(tutorProfileNo);
                FixedLessonReservation fixedLessonReservation = FixedLessonReservation.getFixedLessonReservation(dto, student, tutorProfileInfo);
                fixedLessonReservationRepository.save(fixedLessonReservation);
                lessonReserveService.saveLessonFromFixedLesson(fixedLessonReservation);
            } catch (LessonNotFoundException e) {
                addFailedStudent(failList, dto, LessonUploadFailReason.LESSON_NOT_FOUND);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                addFailedStudent(failList, dto, LessonUploadFailReason.UNKNOWN_ERROR);
            }
        }
        return new FixedLessonUploadResultDto(failList.size(), failList);
    }

    private void addFailedStudent(List<FailedStudentDto> failList, FixedLessonExcelDto dto, LessonUploadFailReason reason) {
        log.info("Failed to process student: {}, Reason: {}", dto, reason);
        failList.add(new FailedStudentDto(
                dto.getName(),
                dto.getPhone(),
                dto.getMemo(),
                reason.getMessage()
        ));
    }

    private boolean isValidDto(FixedLessonExcelDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) return false;
        if (dto.getPhone() == null || !PHONE_PATTERN.matcher(dto.getPhone()).matches()) return false;
        if (dto.getStartTime() == null || dto.getStartTime().isBlank()) return false;
        if (dto.getDayOfWeek() == null || !DAY_OF_WEEK_SET.contains(dto.getDayOfWeek().trim())) return false;
        if (dto.getFirstLessonDate() == null || dto.getFirstLessonDate().isBlank()) return false;
        return true;
    }

}
