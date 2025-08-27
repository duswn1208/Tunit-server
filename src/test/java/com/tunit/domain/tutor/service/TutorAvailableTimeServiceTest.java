package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorAvailableTimeSaveDto;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.exception.TutorProfileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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
class TutorAvailableTimeServiceTest {

    @Autowired
    private TutorAvailableTimeService tutorAvailableTimeService;

    @MockitoBean
    private TutorProfileService tutorProfileService;

    @Test
    @Rollback
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
                    .dayOfWeekNum(dayOfWeek.getValue())
                    .startTime(LocalTime.of(12, 0))
                    .endTime(LocalTime.of(17, 0))
                    .build();
            saveDtos.add(build);
        });

        //when
        tutorAvailableTimeService.saveAvailableTime(1L, saveDtos);

        // then
        List<TutorAvailableTime> savedTimes = tutorAvailableTimeService.findAvailableTimeByTutorProfileNo(1L);

        assertEquals(7, savedTimes.size()); // 요일 수만큼 저장되었는지 확인
        assertTrue(savedTimes.stream().allMatch(t ->
                t.getStartTime().equals(LocalTime.of(12, 0)) &&
                        t.getEndTime().equals(LocalTime.of(17, 0))
        ));
    }

    @Test
    @DisplayName("저장 실패 - 시작 시간이 끝 시간보다 늦으면 안됨")
    @Rollback
    void saveAvailableTime_fail_startAfterEnd() {
        //given
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(1L)
                .tutorProfileNo(1L)
                .build();
        when(tutorProfileService.findByUserNo(any())).thenReturn(tutorProfile);

        List<TutorAvailableTimeSaveDto> saveDtos = new ArrayList<>();
        saveDtos.add(TutorAvailableTimeSaveDto.of()
                .dayOfWeekNum(DayOfWeek.MONDAY.getValue())
                .startTime(LocalTime.MAX)
                .endTime(LocalTime.MIN)
                .build());

        //when & then
        assertThrows(TutorProfileException.class, () -> {
            tutorAvailableTimeService.saveAvailableTime(1L, saveDtos);
        });
    }

    @Test
    @DisplayName("저장 실패 - 같은 요일의 시간이 겹치면 안됨")
    @Rollback
    void saveAvailableTime_fail_overlap() {
        //given
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(1L)
                .tutorProfileNo(1L)
                .build();
        when(tutorProfileService.findByUserNo(any())).thenReturn(tutorProfile);

        List<TutorAvailableTimeSaveDto> saveDtos = new ArrayList<>();
        saveDtos.add(TutorAvailableTimeSaveDto.of()
                .dayOfWeekNum(DayOfWeek.MONDAY.getValue())
                .startTime(LocalTime.NOON)
                .endTime(LocalTime.MIDNIGHT.minusHours(5))
                .build());
        saveDtos.add(TutorAvailableTimeSaveDto.of()
                .dayOfWeekNum(DayOfWeek.MONDAY.getValue())
                .startTime(LocalTime.NOON.plusHours(1))
                .endTime(LocalTime.MIDNIGHT.minusHours(3))
                .build());

        //when & then
        assertThrows(TutorProfileException.class, () -> {
            tutorAvailableTimeService.saveAvailableTime(1L, saveDtos);
        });
    }

    @Test
    @DisplayName("삭제 성공 - 존재하는 시간대 삭제")
    @Rollback
    void deleteAvailableTime_success() {
        //given
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(1L)
                .tutorProfileNo(1L)
                .build();
        when(tutorProfileService.findByUserNo(any())).thenReturn(tutorProfile);

        List<TutorAvailableTimeSaveDto> saveDtos = new ArrayList<>();
        Arrays.stream(DayOfWeek.values()).forEach(dayOfWeek -> {
            TutorAvailableTimeSaveDto build = TutorAvailableTimeSaveDto.of()
                    .dayOfWeekNum(dayOfWeek.getValue())
                    .startTime(LocalTime.MIN)
                    .endTime(LocalTime.MAX)
                    .build();
            saveDtos.add(build);
        });
        tutorAvailableTimeService.saveAvailableTime(1L, saveDtos);

        List<Long> deleteNos = new ArrayList<>();
        deleteNos.add(tutorAvailableTimeService.findAvailableTimeByTutorProfileNo(1L).get(0).getTutorAvailableTimeNo());

        //when
        tutorAvailableTimeService.deleteAvailableTime(1L, deleteNos);

        // then
        assertEquals(6, tutorAvailableTimeService.findAvailableTimeByTutorProfileNo(1L).size());
    }

    @Test
    @DisplayName("삭제 실패 - 존재하지 않는 시간대 삭제 시도")
    @Rollback
    void deleteAvailableTime_fail_notExist() {
        //given
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(1L)
                .tutorProfileNo(1L)
                .build();
        when(tutorProfileService.findByUserNo(any())).thenReturn(tutorProfile);

        List<Long> deleteNos = new ArrayList<>();
        deleteNos.add(999L); // 존재하지 않는 시간대 번호

        //when & then
        assertThrows(TutorProfileException.class, () -> {
            tutorAvailableTimeService.deleteAvailableTime(1L, deleteNos);
        });
    }
}