package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.repository.StudentTutorContractRepository;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Contract 통합 테스트")
class ContractServiceIntegrationTest {

    @Autowired
    private ContractService contractService;

    @Autowired
    private StudentTutorContractRepository contractRepository;

    @Autowired
    private LessonReservationRepository lessonReservationRepository;

    @AfterEach
    void tearDown() {
        lessonReservationRepository.deleteAll();
        contractRepository.deleteAll();
    }

    private List<LocalDateTime> prepareLessonDtList(ContractType contractType, Integer weekCount) {
        List<LocalDateTime> lessonDtList = new ArrayList<>();
        if (contractType.isRegular()) {
            for (int i = 0; i < weekCount * 4; i++) {
                LocalDateTime lessonDt = LocalDateTime.of(2025, 11, 1 + i * 7, 14, 0);
                lessonDtList.add(lessonDt);
            }

            return lessonDtList;
        }

        // FIRSTCOME or TRIAL
        lessonDtList.add(LocalDateTime.of(2025, 11, 15, 10, 0));
        return lessonDtList;
    }

    @Test
    @DisplayName("고정레슨 계약 생성 - lessonDtList에서 가장 빠른 날짜가 startDt로 설정됨, weekCount 필수")
    void createRegularContract_StartDtFromEarliestLesson() {
        // given
        Integer weekCount = 1;
        List<LocalDateTime> lessonDtList = prepareLessonDtList(ContractType.REGULAR, weekCount);

        ContractCreateRequestDto requestDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(100L)
                .studentNo(200L)
                .lessonDtList(lessonDtList) // 순서 섞여있음
                .contractType(ContractType.REGULAR)
                .lessonCategory(LessonSubCategory.BASE)
                .weekCount(weekCount)
                .lessonCount(weekCount * 4)
                .level("중급")
                .place("강남역 스터디카페")
                .emergencyContact("010-1234-5678")
                .totalPrice(400000)
                .source(ContractSource.STUDENT_REQUEST)
                .memo("열심히 하겠습니다")
                .build();

        // when
        ContractResponseDto response = contractService.createContract(requestDto);

        // then - Contract 검증
        LocalDateTime earliestLesson = lessonDtList.stream().min(LocalDateTime::compareTo).orElseThrow();


        assertThat(response).isNotNull();
        assertThat(response.getContractNo()).isNotNull();

        // lessonDtList에서 가장 빠른 날짜가 startDt로 설정되었는지 확인
        assertThat(response.getStartDt()).isEqualTo(earliestLesson.toLocalDate());
        assertThat(response.getDayOfWeekNum()).isEqualTo(DayOfWeek.SUNDAY);
        assertThat(response.getStartTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(response.getEndTime()).isEqualTo(LocalTime.of(15, 0)); // +1시간

        assertThat(response.getContractType()).isEqualTo(ContractType.REGULAR);
        assertThat(response.getContractStatus()).isEqualTo(ContractStatus.ACTIVE);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getLessonName()).isEqualTo("베이스 정규레슨 (주2회)");
        assertThat(response.getWeekCount()).isEqualTo(1); // 정규레슨은 weekCount 있음
        assertThat(response.getLessonCount()).isEqualTo(8);
        assertThat(response.getTotalPrice()).isEqualTo(400000);

        // DB에서 다시 조회하여 검증
        StudentTutorContract savedContract = contractRepository.findById(response.getContractNo()).orElseThrow();
        assertThat(savedContract.getStartDt()).isEqualTo(earliestLesson.toLocalDate());
        assertThat(savedContract.getDayOfWeekNum()).isEqualTo(DayOfWeek.SUNDAY);

        // LessonReservation 생성 검증
        List<LessonReservation> reservations = lessonReservationRepository.findAll();
        assertThat(reservations).hasSize(2);
        assertThat(reservations).allMatch(r -> r.getContractNo().equals(response.getContractNo()));
        assertThat(reservations).allMatch(r -> r.getStatus() == ReservationStatus.REQUESTED);
    }

    @Test
    @DisplayName("주간레슨(선착순) 계약 생성 - weekCount 필수")
    void createFirstComeContract_WithWeekCount() {
        // given
        List<LocalDateTime> lessonDtList = prepareLessonDtList(ContractType.FIRSTCOME, null);

        ContractCreateRequestDto requestDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(100L)
                .studentNo(200L)
                .lessonDtList(lessonDtList)
                .contractType(ContractType.FIRSTCOME)
                .lessonCategory(LessonSubCategory.GUITAR)
                .weekCount(1) // 선착순레슨도 weekCount 필수
                .lessonCount(2)
                .level("중급")
                .place("강남역 스터디카페")
                .totalPrice(100000)
                .source(ContractSource.STUDENT_REQUEST)
                .build();

        // when
        ContractResponseDto response = contractService.createContract(requestDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContractType()).isEqualTo(ContractType.FIRSTCOME);
        assertThat(response.getWeekCount()).isEqualTo(2); // 선착순레슨도 weekCount 있음
        assertThat(response.getLessonCount()).isEqualTo(2);
        assertThat(response.getLessonName()).isEqualTo("기타 정규레슨");

        // LessonReservation 생성 검증
        List<LessonReservation> reservations = lessonReservationRepository.findAll();
        assertThat(reservations).hasSize(2);
    }

    @Test
    @DisplayName("패키지(체험/상담) 계약 생성 - weekCount 없음, TRIAL_REQUESTED 상태")
    void createTrialContract_NoWeekCount() {
        // given
        List<LocalDateTime> lessonDtList = prepareLessonDtList(ContractType.FIRSTCOME, null);

        ContractCreateRequestDto requestDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(100L)
                .studentNo(200L)
                .lessonDtList(lessonDtList)
                .contractType(ContractType.TRIAL)
                .lessonCategory(LessonSubCategory.PIANO)
                .weekCount(null) // 패키지(체험)는 weekCount 없음
                .lessonCount(1)
                .level("입문")
                .place("강남역 음악학원")
                .totalPrice(30000)
                .source(ContractSource.STUDENT_REQUEST)
                .build();

        // when
        ContractResponseDto response = contractService.createContract(requestDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContractType()).isEqualTo(ContractType.TRIAL);
        assertThat(response.getWeekCount()).isNull(); // 패키지는 weekCount 없음
        assertThat(response.getLessonName()).isEqualTo("피아노 상담/체험레슨");
    }

    @Test
    @DisplayName("lessonName 자동 생성 - 사용자가 제공하지 않은 경우")
    void createContract_AutoGenerateLessonName() {
        // given
        List<LocalDateTime> lessonDtList = prepareLessonDtList(ContractType.FIRSTCOME, null);

        ContractCreateRequestDto requestDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(100L)
                .studentNo(200L)
                .lessonDtList(lessonDtList)
                .contractType(ContractType.REGULAR)
                .lessonCategory(LessonSubCategory.PIANO)
                .weekCount(3)
                .lessonCount(12)
                .lessonName(null) // lessonName 제공하지 않음
                .level("고급")
                .totalPrice(600000)
                .source(ContractSource.STUDENT_REQUEST)
                .build();

        // when
        ContractResponseDto response = contractService.createContract(requestDto);

        // then
        assertThat(response.getLessonName()).isEqualTo("피아노 정규레슨 (주3회)");
    }

    @Test
    @DisplayName("lessonName 사용자 지정 - 사용자가 제공한 경우")
    void createContract_CustomLessonName() {
        // given
        LocalDateTime lessonDt = LocalDateTime.of(2025, 11, 10, 14, 0);
        String customName = "맞춤 영어회화 레슨";

        ContractCreateRequestDto requestDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(100L)
                .studentNo(200L)
                .lessonDtList(List.of(lessonDt))
                .contractType(ContractType.REGULAR)
                .lessonCategory(LessonSubCategory.COOKING)
                .weekCount(2)
                .lessonCount(8)
                .lessonName(customName) // 사용자가 직접 제공
                .level("중급")
                .totalPrice(400000)
                .source(ContractSource.STUDENT_REQUEST)
                .build();

        // when
        ContractResponseDto response = contractService.createContract(requestDto);

        // then
        assertThat(response.getLessonName()).isEqualTo(customName);
    }

    @Test
    @DisplayName("계약 생성 실패 - lessonDtList가 비어있는 경우")
    void createContract_EmptyLessonDtList() {
        // given
        ContractCreateRequestDto requestDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(100L)
                .studentNo(200L)
                .lessonDtList(List.of()) // 비어있음
                .contractType(ContractType.REGULAR)
                .lessonCategory(LessonSubCategory.BAKING)
                .weekCount(2)
                .lessonCount(8)
                .level("중급")
                .totalPrice(400000)
                .source(ContractSource.STUDENT_REQUEST)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.createContract(requestDto))
                .isInstanceOf(ContractException.class)
                .hasMessage("레슨 매칭은 최소 하나 이상의 레슨 일정이 필요합니다.");
    }

    @Test
    @DisplayName("학생의 계약 목록 조회")
    void getStudentContracts() {
        // given
        Long studentNo = 200L;

        // 계약 2개 생성
        createTestContract(100L, studentNo, ContractType.REGULAR);
        createTestContract(101L, studentNo, ContractType.TRIAL);

        // when
        List<ContractResponseDto> contracts = contractService.getStudentContracts(studentNo);

        // then
        assertThat(contracts).hasSize(2);
        assertThat(contracts).allMatch(c -> c.getStudentNo().equals(studentNo));
    }

    @Test
    @DisplayName("튜터의 계약 목록 조회")
    void getTutorContracts() {
        // given
        Long tutorProfileNo = 100L;

        // 계약 2개 생성
        createTestContract(tutorProfileNo, 200L, ContractType.REGULAR);
        createTestContract(tutorProfileNo, 201L, ContractType.FIRSTCOME);

        // when
        List<ContractResponseDto> contracts = contractService.getTutorContracts(tutorProfileNo);

        // then
        assertThat(contracts).hasSize(2);
        assertThat(contracts).allMatch(c -> c.getTutorProfileNo().equals(tutorProfileNo));
    }

    @Test
    @DisplayName("계약 단건 조회")
    void getContract() {
        // given
        Long tutorProfileNo = 100L;
        Long studentNo = 200L;
        ContractResponseDto created = createTestContract(tutorProfileNo, studentNo, ContractType.REGULAR);

        // when
        ContractResponseDto found = contractService.getContract(created.getContractNo());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getContractNo()).isEqualTo(created.getContractNo());
        assertThat(found.getTutorProfileNo()).isEqualTo(tutorProfileNo);
        assertThat(found.getStudentNo()).isEqualTo(studentNo);
    }

    @Test
    @DisplayName("계약 조회 실패 - 존재하지 않는 계약")
    void getContract_NotFound() {
        // when & then
        assertThatThrownBy(() -> contractService.getContract(9999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("계약을 찾을 수 없습니다.");
    }

    // 테스트용 계약 생성 헬퍼 메서드
    private ContractResponseDto createTestContract(Long tutorProfileNo, Long studentNo, ContractType contractType) {
        List<LocalDateTime> lessonDtList = prepareLessonDtList(contractType, 2);

        ContractCreateRequestDto requestDto = ContractCreateRequestDto.builder()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(studentNo)
                .lessonDtList(lessonDtList)
                .contractType(contractType)
                .lessonCategory(LessonSubCategory.YOGA)
                .weekCount(contractType == ContractType.TRIAL ? null : 2) // TRIAL만 weekCount 없음, REGULAR/FIRSTCOME은 있음
                .lessonCount(1)
                .level("중급")
                .place("강남역")
                .totalPrice(100000)
                .source(ContractSource.STUDENT_REQUEST)
                .build();

        return contractService.createContract(requestDto);
    }
}

