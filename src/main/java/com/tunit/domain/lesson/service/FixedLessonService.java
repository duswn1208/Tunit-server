package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.LessonUploadFailReason;
import com.tunit.domain.lesson.dto.FailedStudentDto;
import com.tunit.domain.lesson.dto.FixedLessonSaveDto;
import com.tunit.domain.lesson.dto.FixedLessonUploadResultDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.FixedLessonReservationRepository;
import com.tunit.domain.lesson.util.FixedLessonExcelRowParser;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.exception.UserException;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
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
                student = getOrCreateWaitingStudent(dto.studentName(), dto.phone());
            } catch (Exception e) {
                addFailedStudent(failList, dto, LessonUploadFailReason.USER_SIGNUP_FAIL);
                continue;
            }
            if (student == null) continue;

            try {

                dto.dayOfWeekSet().forEach(dayOfWeek -> {
                    TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutorProfileInfo(tutorProfileNo);
                    FixedLessonReservation fixedLessonReservation = FixedLessonReservation.getFixedLessonReservation(dto, dayOfWeek, student, tutorProfileInfo);
                    existLesson(tutorProfileNo, dayOfWeek.getValue(), student);
                    fixedLessonReservationRepository.save(fixedLessonReservation);
                    lessonReserveService.saveLessonFromFixedLessonFromExcel(fixedLessonReservation);
                });
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
        UserMain student = getOrCreateWaitingStudent(fixedLessonSaveDto.studentName(), fixedLessonSaveDto.phone());
        if (student == null) {
            throw new UserException("학생 조회 혹은 생성에 실패했습니다.");
        }

        // 2. 레슨 조회 및 저장
        TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutorProfileInfo(tutorProfileNo);

        fixedLessonSaveDto.dayOfWeekSet().forEach(dayOfWeek -> {
            FixedLessonReservation fixedLessonReservation = FixedLessonReservation.getFixedLessonReservation(fixedLessonSaveDto, dayOfWeek, student, tutorProfileInfo);
            existLesson(tutorProfileNo, dayOfWeek.getValue(), student);
            fixedLessonReservationRepository.save(fixedLessonReservation);
            lessonReserveService.saveLessonFromFixedLessonFromWeb(fixedLessonReservation);
        });

    }

    private void existLesson(Long tutorProfileNo, Integer dayOfWeekNum, UserMain student) {
        //기존 예약과 중복체크
        boolean exists = fixedLessonReservationRepository.existsByTutorProfileNoAndStudentNoAndDayOfWeekNum(
                tutorProfileNo,
                student.getUserNo(),
                dayOfWeekNum
        );
        if (exists) {
            throw new LessonNotFoundException("이미 동일한 요일에 등록된 학생이 있습니다. 중복 등록은 불가능합니다.");
        }
    }

    private UserMain getOrCreateWaitingStudent(String name, String phone) {
        UserMain student = userService.findByNameAndPhone(name, phone);
        if (student == null) {
            student = userService.saveWaitingStudent(name, phone);
        }
        return student;
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
