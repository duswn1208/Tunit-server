package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.dto.FixedLessonSaveDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.exception.LessonDuplicationException;
import com.tunit.domain.lesson.repository.FixedLessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedLessonService {
    private final FixedLessonReservationRepository fixedLessonReservationRepository;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final LessonReserveProcessorService lessonReserveService;

    @Transactional
    public void saveFixedLesson(Long tutorProfileNo, FixedLessonSaveDto fixedLessonSaveDto) {
        UserMain student = userService.getOrCreateWaitingStudent(fixedLessonSaveDto.studentName(), fixedLessonSaveDto.phone());

        // 2. 레슨 조회 및 저장
        TutorProfileResponseDto tutorProfileInfo = tutorProfileService.findTutor(tutorProfileNo);

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

}
