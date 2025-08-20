package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorAvailableTimeSaveDto;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.exception.TutorProfileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class TutorLessonTimeServiceTest {

    @Autowired
    private TutorLessonTimeService tutorLessonTimeService;

    @MockitoBean
    private TutorProfileService tutorProfileService;

    @Test
    void saveAvailableTime() {
        //given
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(1L)
                .tutorProfileNo(1L)
                .build();
        when(tutorProfileService.findByUserNo(any())).thenReturn(tutorProfile);

        List<TutorAvailableTimeSaveDto> saveDtos = new ArrayList<>();
        Arrays.stream(DayOfWeek.values()).forEach(dayOfWeek -> {
            TutorAvailableTimeSaveDto build = TutorAvailableTimeSaveDto.of()
                    .dayOfWeek(dayOfWeek)
                    .startTime(LocalTime.NOON)
                    .endTime(LocalTime.MIDNIGHT)
                    .build();
            saveDtos.add(build);
        });

        //when
        tutorLessonTimeService.saveAvailableTime(1L, saveDtos);

        // then
        List<TutorAvailableTime> savedTimes = tutorLessonTimeService.findAvailableTimeByUserNo(1L);

        assertEquals(7, savedTimes.size()); // 요일 수만큼 저장되었는지 확인
        assertTrue(savedTimes.stream().allMatch(t ->
                t.getStartTime().equals(LocalTime.NOON) &&
                        t.getEndTime().equals(LocalTime.MIDNIGHT)
        ));
    }

    @Test
    @DisplayName("저장 실패 - 시작 시간이 끝 시간보다 늦으면 안됨")
    void saveAvailableTime_fail_startAfterEnd() {
        //given
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(1L)
                .tutorProfileNo(1L)
                .build();
        when(tutorProfileService.findByUserNo(any())).thenReturn(tutorProfile);

        List<TutorAvailableTimeSaveDto> saveDtos = new ArrayList<>();
        saveDtos.add(TutorAvailableTimeSaveDto.of()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.MAX)
                .endTime(LocalTime.MIN)
                .build());

        //when & then
        assertThrows(TutorProfileException.class, () -> {
            tutorLessonTimeService.saveAvailableTime(1L, saveDtos);
        });
    }

    @Test
    @DisplayName("저장 실패 - 같은 요일의 시간이 겹치면 안됨")
    void saveAvailableTime_fail_overlap() {
        //given
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(1L)
                .tutorProfileNo(1L)
                .build();
        when(tutorProfileService.findByUserNo(any())).thenReturn(tutorProfile);

        List<TutorAvailableTimeSaveDto> saveDtos = new ArrayList<>();
        saveDtos.add(TutorAvailableTimeSaveDto.of()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.NOON)
                .endTime(LocalTime.MIDNIGHT.minusHours(5))
                .build());
        saveDtos.add(TutorAvailableTimeSaveDto.of()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.NOON.plusHours(1))
                .endTime(LocalTime.MIDNIGHT.minusHours(3))
                .build());

        //when & then
        assertThrows(TutorProfileException.class, () -> {
            tutorLessonTimeService.saveAvailableTime(1L, saveDtos);
        });
    }

    @Test
    void saveHoliday() {
    }
}