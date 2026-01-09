package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractExcelSaveDto;
import com.tunit.domain.contract.dto.ContractExcelUploadResultDto;
import com.tunit.domain.contract.dto.ContractScheduleDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.util.ContractExcelRowParser;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.service.LessonReserveProcessorService;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractExcelService 단위 테스트")
class ContractExcelServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TutorProfileService tutorProfileService;

    @Mock
    private ContractExcelRowParser contractExcelRowParser;

    @Mock
    private LessonReserveProcessorService lessonReserveService;

    @Mock
    private ContractQueryService contractQueryService;

    @InjectMocks
    private ContractExcelService contractExcelService;

    @Test
    @DisplayName("정규 레슨 엑셀 업로드 성공")
    void uploadExcelWithContract_Regular_Success() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0)),
                        new ContractScheduleDto(3, "수", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6), // 월요일
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                Integer.valueOf(2),
                Integer.valueOf(50000),
                "초급",
                "강남역",
                "010-9999-9999",
                "테스트 메모"
        );

        UserMain student = UserMain.of()
                .userNo(100L)
                .name("홍길동")
                .phone("010-1234-5678")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.STUDENT)
                .build();

        StudentTutorContract contract = StudentTutorContract.builder()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(student.getUserNo())
                .contractType(ContractType.REGULAR)
                .contractStatus(ContractStatus.REQUESTED)
                .lessonCount(8)
                .weekCount(2)
                .totalPrice(500000)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        TutorProfileResponseDto tutorProfile = new TutorProfileResponseDto(
                1L, tutorProfileNo, "튜터1", null, null, null, null, null, null,
                5, 50000, 60
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(dto));
        when(userService.getOrCreateWaitingStudent("홍길동", "010-1234-5678")).thenReturn(student);
        when(contractQueryService.createContract(any(), 60)).thenReturn(contract);
        when(tutorProfileService.findTutor(tutorProfileNo)).thenReturn(tutorProfile);

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(0);
        assertThat(result.failList()).isEmpty();
        verify(contractQueryService, times(1)).createContract(any(), 60);
        verify(lessonReserveService, times(2)).reserveLessonsBatch(eq(contract), anyList());
    }

    @Test
    @DisplayName("선착순 레슨 엑셀 업로드 성공")
    void uploadExcelWithContract_FirstCome_Success() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "김철수",
                "010-2222-3333",
                List.of(
                        new ContractScheduleDto(2, "화", LocalTime.of(15, 0), LocalTime.of(16, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 7),
                ReservationStatus.REQUESTED,
                ContractType.FIRSTCOME,
                Integer.valueOf(1),
                Integer.valueOf(100000),
                null,
                null,
                null,
                null
        );

        UserMain student = UserMain.of()
                .userNo(200L)
                .name("김철수")
                .phone("010-2222-3333")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.STUDENT)
                .build();

        StudentTutorContract contract = StudentTutorContract.builder()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(student.getUserNo())
                .contractType(ContractType.FIRSTCOME)
                .contractStatus(ContractStatus.REQUESTED)
                .lessonCount(1)
                .totalPrice(100000)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(dto));
        when(userService.getOrCreateWaitingStudent("김철수", "010-2222-3333")).thenReturn(student);
        when(contractQueryService.createContract(any(), 60)).thenReturn(contract);

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(0);
        assertThat(result.failList()).isEmpty();
        verify(contractQueryService, times(1)).createContract(any(), 60);
        verify(lessonReserveService, times(1)).processTutorCreate(eq(tutorProfileNo), any());
    }

    @Test
    @DisplayName("체험 레슨 엑셀 업로드 성공")
    void uploadExcelWithContract_Trial_Success() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "이영희",
                "010-3333-4444",
                List.of(
                        new ContractScheduleDto(5, "금", LocalTime.of(16, 0), LocalTime.of(17, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 10),
                ReservationStatus.REQUESTED,
                ContractType.TRIAL,
                Integer.valueOf(1),
                Integer.valueOf(50000),
                null,
                null,
                null,
                null
        );

        UserMain student = UserMain.of()
                .userNo(300L)
                .name("이영희")
                .phone("010-3333-4444")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.STUDENT)
                .build();

        StudentTutorContract contract = StudentTutorContract.builder()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(student.getUserNo())
                .contractType(ContractType.TRIAL)
                .contractStatus(ContractStatus.REQUESTED)
                .lessonCount(1)
                .totalPrice(50000)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(dto));
        when(userService.getOrCreateWaitingStudent("이영희", "010-3333-4444")).thenReturn(student);
        when(contractQueryService.createContract(any(), 60)).thenReturn(contract);

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(0);
        assertThat(result.failList()).isEmpty();
        verify(contractQueryService, times(1)).createContract(any(), 60);
        verify(lessonReserveService, times(1)).processTutorCreate(eq(tutorProfileNo), any());
    }

    @Test
    @DisplayName("필수 필드 누락 시 실패 목록에 추가")
    void uploadExcelWithContract_MissingRequiredField_AddToFailList() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto invalidDto = new ContractExcelSaveDto(
                null, // 이름 누락
                "010-1234-5678",
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                Integer.valueOf(2),
                Integer.valueOf(50000),
                null,
                null,
                null,
                null
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(invalidDto));

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList()).hasSize(1);
        verify(contractQueryService, never()).createContract(any(), 60);
    }

    @Test
    @DisplayName("전화번호 형식이 잘못된 경우 실패 목록에 추가")
    void uploadExcelWithContract_InvalidPhoneFormat_AddToFailList() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto invalidDto = new ContractExcelSaveDto(
                "홍길동",
                "123-456-7890", // 잘못된 전화번호 형식
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                Integer.valueOf(2),
                Integer.valueOf(50000),
                null,
                null,
                null,
                null
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(invalidDto));

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList()).hasSize(1);
        verify(contractQueryService, never()).createContract(any(), 60);
    }

    @Test
    @DisplayName("학생 생성 실패 시 실패 목록에 추가")
    void uploadExcelWithContract_UserCreationFail_AddToFailList() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                Integer.valueOf(2),
                Integer.valueOf(50000),
                null,
                null,
                null,
                null
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(dto));
        when(userService.getOrCreateWaitingStudent("홍길동", "010-1234-5678"))
                .thenThrow(new RuntimeException("User creation failed"));

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList()).hasSize(1);
        verify(contractQueryService, never()).createContract(any(), 60);
    }

    @Test
    @DisplayName("여러 학생 데이터 중 일부만 실패")
    void uploadExcelWithContract_PartialFailure() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto validDto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                Integer.valueOf(2),
                Integer.valueOf(50000),
                null,
                null,
                null,
                null
        );

        ContractExcelSaveDto invalidDto = new ContractExcelSaveDto(
                null, // 이름 누락
                "010-9999-9999",
                List.of(
                        new ContractScheduleDto(2, "화", LocalTime.of(15, 0), LocalTime.of(16, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 7),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                Integer.valueOf(2),
                Integer.valueOf(50000),
                null,
                null,
                null,
                null
        );

        UserMain student = UserMain.of()
                .userNo(100L)
                .name("홍길동")
                .phone("010-1234-5678")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.STUDENT)
                .build();

        StudentTutorContract contract = StudentTutorContract.builder()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(student.getUserNo())
                .contractType(ContractType.REGULAR)
                .contractStatus(ContractStatus.REQUESTED)
                .lessonCount(8)
                .weekCount(1)
                .totalPrice(500000)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        TutorProfileResponseDto tutorProfile = new TutorProfileResponseDto(
                1L, tutorProfileNo, "튜터1", null, null, null, null, null, null,
                5, 50000, 60
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(validDto, invalidDto));
        when(userService.getOrCreateWaitingStudent("홍길동", "010-1234-5678")).thenReturn(student);
        when(contractQueryService.createContract(any(), 60)).thenReturn(contract);
        when(tutorProfileService.findTutor(tutorProfileNo)).thenReturn(tutorProfile);

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList()).hasSize(1);
        verify(contractQueryService, times(1)).createContract(any(), 60);
    }

    @Test
    @DisplayName("계약 타입이 null인 경우 실패")
    void uploadExcelWithContract_NullContractType_AddToFailList() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                null, // 계약 타입 누락
                Integer.valueOf(2),
                Integer.valueOf(50000),
                null,
                null,
                null,
                null
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(dto));

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList()).hasSize(1);
        verify(contractQueryService, never()).createContract(any(), 60);
    }

    @Test
    @DisplayName("총 가격이 null인 경우 실패")
    void uploadExcelWithContract_NullTotalPrice_AddToFailList() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                null, // 가격 누락
                Integer.valueOf(8),
                null,
                null,
                null,
                null
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(dto));

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList()).hasSize(1);
        verify(contractQueryService, never()).createContract(any(), 60);
    }

    @Test
    @DisplayName("레슨 횟수가 null인 경우 실패")
    void uploadExcelWithContract_NullLessonCount_AddToFailList() {
        // given
        Long tutorProfileNo = 1L;
        MultipartFile file = mock(MultipartFile.class);

        ContractExcelSaveDto dto = new ContractExcelSaveDto(
                "홍길동",
                "010-1234-5678",
                List.of(
                        new ContractScheduleDto(1, "월", LocalTime.of(14, 0), LocalTime.of(15, 0))
                ),
                LessonSubCategory.ENGLISH,
                LocalDate.of(2025, 1, 6),
                ReservationStatus.REQUESTED,
                ContractType.REGULAR,
                Integer.valueOf(50000),
                null, // 레슨 횟수 누락
                null,
                null,
                null,
                null
        );

        when(contractExcelRowParser.parseWithContract(file)).thenReturn(List.of(dto));

        // when
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);

        // then
        assertThat(result.failCount()).isEqualTo(1);
        assertThat(result.failList()).hasSize(1);
        verify(contractQueryService, never()).createContract(any(), 60);
    }
}
