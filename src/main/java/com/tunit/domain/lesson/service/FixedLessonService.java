package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.LessonUploadFailReason;
import com.tunit.domain.lesson.dto.FailedStudentDto;
import com.tunit.domain.lesson.dto.FixedLessonSaveDto;
import com.tunit.domain.lesson.dto.FixedLessonUploadResultDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.exception.LessonDuplicationException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedLessonService {
    private final FixedLessonReservationRepository fixedLessonReservationRepository;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final FixedLessonExcelRowParser fixedLessonExcelRowParser;
    private final LessonReserveService lessonReserveService;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[016789]-?\\d{3,4}-?\\d{4}$");

    @Transactional
    public FixedLessonUploadResultDto uploadExcel(@NonNull Long tutorProfileNo, MultipartFile file) {
        List<FixedLessonSaveDto> rows = fixedLessonExcelRowParser.parse(file);
        List<FailedStudentDto> failList = new ArrayList<>();
        for (FixedLessonSaveDto dto : rows) {
            if (!isValidDto(dto)) {
                addFailedStudent(failList, dto, LessonUploadFailReason.REQUIRED_FIELD_MISSING);
                continue;
            }

            UserMain student;
            try {
                student = userService.getOrCreateWaitingStudent(dto.studentName(), dto.phone());
            } catch (Exception e) {
                addFailedStudent(failList, dto, LessonUploadFailReason.USER_SIGNUP_FAIL);
                continue;
            }
            if (student == null) continue;

            try {
                dto.dayOfWeekSet().forEach(dayOfWeek -> {
                    TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutorProfileInfoByTutorProfileNo(tutorProfileNo);
                    FixedLessonReservation fixedLessonReservation = FixedLessonReservation.getFixedLessonReservation(dto, dayOfWeek, student, tutorProfileInfo);
                    existLesson(fixedLessonReservation);
                    fixedLessonReservationRepository.save(fixedLessonReservation);
                    lessonReserveService.saveLessonFromFixedLessonFromExcel(fixedLessonReservation);
                });

            } catch (LessonDuplicationException e) {
                addFailedStudent(failList, dto, LessonUploadFailReason.LESSON_DUPLICATION);
            } catch (LessonNotFoundException e) {
                addFailedStudent(failList, dto, LessonUploadFailReason.LESSON_NOT_FOUND);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                addFailedStudent(failList, dto, LessonUploadFailReason.UNKNOWN_ERROR);
            }
        }
        return new FixedLessonUploadResultDto(failList.size(), failList);
    }

    @Transactional
    public void saveFixedLesson(Long tutorProfileNo, FixedLessonSaveDto fixedLessonSaveDto) {
        UserMain student = userService.getOrCreateWaitingStudent(fixedLessonSaveDto.studentName(), fixedLessonSaveDto.phone());

        // 2. 레슨 조회 및 저장
        TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutorProfileInfoByTutorProfileNo(tutorProfileNo);

        fixedLessonSaveDto.dayOfWeekSet().forEach(dayOfWeek -> {
            FixedLessonReservation fixedLessonReservation = FixedLessonReservation.getFixedLessonReservation(fixedLessonSaveDto, dayOfWeek, student, tutorProfileInfo);
            existLesson(fixedLessonReservation);
            fixedLessonReservationRepository.save(fixedLessonReservation);

            //동일한 시간대 4번 반복 저장
            lessonReserveService.saveLessonFromFixedLessonFromWeb(fixedLessonReservation);
        });

    }

    private void existLesson(FixedLessonReservation fixedLessonReservation) {
        //기존 예약과 중복체크
        boolean exists = fixedLessonReservationRepository.existsByTutorProfileNoAndDayOfWeekNumAndStartTimeAfterAndEndTimeBefore(
                fixedLessonReservation.getTutorProfileNo(),
                fixedLessonReservation.getDayOfWeekNum(),
                fixedLessonReservation.getStartTime(),
                fixedLessonReservation.getEndTime()
        );
        if (exists) {
            throw new LessonDuplicationException();
        }
    }

    private void addFailedStudent(List<FailedStudentDto> failList, FixedLessonSaveDto dto, LessonUploadFailReason reason) {
        log.info("Failed to process student: {}, Reason: {}", dto, reason);
        failList.add(new FailedStudentDto(
                dto.studentName(),
                dto.phone(),
                dto.memo(),
                reason.getMessage()
        ));
    }

    private boolean isValidDto(FixedLessonSaveDto dto) {
        if (dto.studentName() == null || dto.studentName().isBlank()) return false;
        if (dto.phone() == null || !PHONE_PATTERN.matcher(dto.phone()).matches()) return false;
        if (dto.startTime() == null) return false;
        if (dto.dayOfWeekSet() == null) return false;
        if (dto.firstLessonDate() == null) return false;
        return true;
    }

}
