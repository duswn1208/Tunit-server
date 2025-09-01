package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.dto.FixedLessonExcelDto;
import com.tunit.domain.lesson.dto.FixedLessonUploadResultDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.FixedLessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import com.tunit.domain.lesson.util.FixedLessonExcelRowParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FixedLessonExcelServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private FixedLessonReservationRepository fixedLessonReservationRepository;
    @Mock
    private TutorProfileService tutorProfileService;
    @Mock
    private FixedLessonExcelRowParser fixedLessonExcelRowParser;
    @InjectMocks
    private FixedLessonExcelService fixedLessonExcelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadExcel_success() {
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);
        FixedLessonExcelDto dto = new FixedLessonExcelDto();
        dto.setName("홍길동");
        dto.setPhone("010-1234-5678");
        dto.setStartTime("14:00");
        dto.setDayOfWeek("월");
        dto.setLesson("영어");
        dto.setFirstLessonDate("2025-09-01");
        dto.setMemo("메모");
        List<FixedLessonExcelDto> dtos = List.of(dto);
        when(fixedLessonExcelRowParser.parse(file)).thenReturn(dtos);
        when(userService.findByNameAndPhone(any(), any())).thenReturn(new UserMain());
        when(tutorProfileService.findTutorProfileInfo(any())).thenReturn(mock(TutorProfileResponseDto.class));
        when(fixedLessonReservationRepository.save(any())).thenReturn(new FixedLessonReservation());

        FixedLessonUploadResultDto result = fixedLessonExcelService.uploadExcel(tutorProfileNo, file);
        assertThat(result.failCount()).isEqualTo(0);
        assertThat(result.failList()).isEmpty();
    }

    @Test
    void uploadExcel_invalidDto() {
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);
        FixedLessonExcelDto dto = new FixedLessonExcelDto(); // 필수값 없음
        List<FixedLessonExcelDto> dtos = List.of(dto);
        when(fixedLessonExcelRowParser.parse(file)).thenReturn(dtos);

        FixedLessonUploadResultDto result = fixedLessonExcelService.uploadExcel(tutorProfileNo, file);
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList().get(0).reason()).isEqualTo("필수값 누락");
    }

    @Test
    void uploadExcel_userSaveFail() {
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);
        FixedLessonExcelDto dto = new FixedLessonExcelDto();
        dto.setName("홍길동");
        dto.setPhone("010-1234-5678");
        dto.setStartTime("14:00");
        dto.setDayOfWeek("월");
        dto.setLesson("영어");
        dto.setFirstLessonDate("2025-09-01");
        List<FixedLessonExcelDto> dtos = List.of(dto);
        when(fixedLessonExcelRowParser.parse(file)).thenReturn(dtos);
        when(userService.findByNameAndPhone(any(), any())).thenReturn(null);
        when(userService.saveWaitingStudent(any(), any())).thenThrow(new RuntimeException("회원가입 실패"));

        FixedLessonUploadResultDto result = fixedLessonExcelService.uploadExcel(tutorProfileNo, file);
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList().get(0).reason()).isEqualTo("회원가입 실패");
    }

    @Test
    void uploadExcel_lessonNotFound() {
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);
        FixedLessonExcelDto dto = new FixedLessonExcelDto();
        dto.setName("홍길동");
        dto.setPhone("010-1234-5678");
        dto.setStartTime("14:00");
        dto.setDayOfWeek("월");
        dto.setLesson("영어");
        dto.setFirstLessonDate("2025-09-01");
        List<FixedLessonExcelDto> dtos = List.of(dto);
        when(fixedLessonExcelRowParser.parse(file)).thenReturn(dtos);
        when(userService.findByNameAndPhone(any(), any())).thenReturn(new UserMain());
        when(tutorProfileService.findTutorProfileInfo(any())).thenReturn(mock(TutorProfileResponseDto.class));
        when(fixedLessonReservationRepository.save(any())).thenThrow(new LessonNotFoundException("레슨 정보 오류"));

        FixedLessonUploadResultDto result = fixedLessonExcelService.uploadExcel(tutorProfileNo, file);
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList().get(0).reason()).isEqualTo("레슨 정보 오류");
    }
}
