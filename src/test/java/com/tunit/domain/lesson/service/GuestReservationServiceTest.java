package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationSource;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.GuestReservationRequestDto;
import com.tunit.domain.lesson.dto.GuestReservationResponseDto;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("비회원 예약 서비스 테스트")
class GuestReservationServiceTest {

    @Autowired
    private GuestReservationService guestReservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private LessonReservationRepository lessonReservationRepository;

    private TutorProfile testTutorProfile;
    private UserMain testTutor;

    @BeforeEach
    void setUp() {
        // 테스트용 선생님 생성
        testTutor = UserMain.of()
                .userId("test_tutor")
                .name("테스트선생님")
                .nickname("튜터닉네임")
                .phone("01011112222")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.TUTOR)
                .build();
        testTutor = userRepository.save(testTutor);

        testTutorProfile = TutorProfile.of()
                .userNo(testTutor.getUserNo())
                .introduce("테스트 소개")
                .durationMin(60)
                .pricePerHour(50000)
                .careerYears(5)
                .build();
        testTutorProfile = tutorProfileRepository.save(testTutorProfile);
    }

    @AfterEach
    void tearDown() {
        lessonReservationRepository.deleteAll();
        tutorProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("비회원 예약 생성 - 신규 학생")
    void createGuestReservation_NewStudent() {
        // given
        GuestReservationRequestDto request = new GuestReservationRequestDto(
                "김학생",
                "01099998888",
                LessonSubCategory.PIANO,
                LocalDate.now().plusDays(3),
                LocalTime.of(14, 0)
        );

        // when
        String magicLink = guestReservationService.createGuestReservation(
                testTutorProfile.getTutorProfileNo(), 
                request
        );

        // then
        assertThat(magicLink).isNotNull().contains("/reservations/verify/");

        // 학생이 생성되었는지 확인
        UserMain student = userRepository.findByPhone("01099998888").orElse(null);
        assertThat(student).isNotNull();
        assertThat(student.getName()).isEqualTo("김학생");
        assertThat(student.getUserStatus()).isEqualTo(UserStatus.WAITING);
        assertThat(student.getUserRole()).isEqualTo(UserRole.STUDENT);

        // 예약이 생성되었는지 확인
        LessonReservation reservation = lessonReservationRepository.findAll().get(0);
        assertThat(reservation).isNotNull();
        assertThat(reservation.getStudentNo()).isEqualTo(student.getUserNo());
        assertThat(reservation.getTutorProfileNo()).isEqualTo(testTutorProfile.getTutorProfileNo());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REQUESTED);
        assertThat(reservation.getSource()).isEqualTo(ReservationSource.GUEST);
        assertThat(reservation.getLessonCategory()).isEqualTo(LessonSubCategory.PIANO);
        assertThat(reservation.getEndTime()).isEqualTo(LocalTime.of(15, 0)); // 60분 후
    }

    @Test
    @DisplayName("비회원 예약 생성 - 기존 학생 (전화번호 중복)")
    void createGuestReservation_ExistingStudent() {
        // given
        UserMain existingStudent = UserMain.saveWaitingStudent("김학생", "01099998888");
        existingStudent = userRepository.save(existingStudent);

        GuestReservationRequestDto request = new GuestReservationRequestDto(
                "김학생",
                "01099998888",
                LessonSubCategory.PIANO,
                LocalDate.now().plusDays(3),
                LocalTime.of(14, 0)
        );

        // when
        String magicLink = guestReservationService.createGuestReservation(
                testTutorProfile.getTutorProfileNo(),
                request
        );

        // then
        assertThat(magicLink).isNotNull();

        // 학생이 중복 생성되지 않았는지 확인
        long studentCount = userRepository.findAll().stream()
                .filter(u -> "01099998888".equals(u.getPhone()))
                .count();
        assertThat(studentCount).isEqualTo(1);

        // 예약이 기존 학생으로 생성되었는지 확인
        LessonReservation reservation = lessonReservationRepository.findAll().get(0);
        assertThat(reservation.getStudentNo()).isEqualTo(existingStudent.getUserNo());
    }

    @Test
    @DisplayName("마법 링크로 예약 조회")
    void getReservationByToken() {
        // given
        GuestReservationRequestDto request = new GuestReservationRequestDto(
                "박학생",
                "01077776666",
                LessonSubCategory.GUITAR,
                LocalDate.now().plusDays(5),
                LocalTime.of(10, 0)
        );

        String magicLink = guestReservationService.createGuestReservation(
                testTutorProfile.getTutorProfileNo(),
                request
        );

        String token = magicLink.substring(magicLink.lastIndexOf("/") + 1);

        // when
        GuestReservationResponseDto response = guestReservationService.getReservationByToken(token);

        // then
        assertThat(response).isNotNull();
        assertThat(response.studentName()).isEqualTo("박학생");
        assertThat(response.tutorName()).isEqualTo("튜터닉네임");
        assertThat(response.lessonCategory()).isEqualTo("GUITAR");
        assertThat(response.status()).isEqualTo(ReservationStatus.REQUESTED);
        assertThat(response.startTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(response.endTime()).isEqualTo(LocalTime.of(11, 0));
    }

    @Test
    @DisplayName("예약 승인 처리")
    void handleGuestAction_Confirm() {
        // given
        GuestReservationRequestDto request = new GuestReservationRequestDto(
                "이학생",
                "01055554444",
                LessonSubCategory.VOCAL,
                LocalDate.now().plusDays(7),
                LocalTime.of(16, 0)
        );

        String magicLink = guestReservationService.createGuestReservation(
                testTutorProfile.getTutorProfileNo(),
                request
        );

        String token = magicLink.substring(magicLink.lastIndexOf("/") + 1);

        // when
        guestReservationService.handleGuestAction(token, "confirm");

        // then
        LessonReservation reservation = lessonReservationRepository.findAll().get(0);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ACTIVE);
    }

    @Test
    @DisplayName("예약 거절 처리")
    void handleGuestAction_Reject() {
        // given
        GuestReservationRequestDto request = new GuestReservationRequestDto(
                "최학생",
                "01033332222",
                LessonSubCategory.DRUM,
                LocalDate.now().plusDays(7),
                LocalTime.of(16, 0)
        );

        String magicLink = guestReservationService.createGuestReservation(
                testTutorProfile.getTutorProfileNo(),
                request
        );

        String token = magicLink.substring(magicLink.lastIndexOf("/") + 1);

        // when
        guestReservationService.handleGuestAction(token, "reject");

        // then
        LessonReservation reservation = lessonReservationRepository.findAll().get(0);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 예약 조회 시 예외 발생")
    void getReservationByToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token.here";

        // when & then
        assertThatThrownBy(() -> guestReservationService.getReservationByToken(invalidToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 토큰");
    }

    @Test
    @DisplayName("존재하지 않는 선생님 프로필로 예약 시 예외 발생")
    void createGuestReservation_TutorNotFound() {
        // given
        GuestReservationRequestDto request = new GuestReservationRequestDto(
                "정학생",
                "01011113333",
                LessonSubCategory.PIANO,
                LocalDate.now().plusDays(3),
                LocalTime.of(14, 0)
        );

        // when & then
        assertThatThrownBy(() -> guestReservationService.createGuestReservation(999999L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("선생님 프로필을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("잘못된 액션으로 예약 처리 시 예외 발생")
    void handleGuestAction_InvalidAction() {
        // given
        GuestReservationRequestDto request = new GuestReservationRequestDto(
                "강학생",
                "01044445555",
                LessonSubCategory.PIANO,
                LocalDate.now().plusDays(3),
                LocalTime.of(14, 0)
        );

        String magicLink = guestReservationService.createGuestReservation(
                testTutorProfile.getTutorProfileNo(),
                request
        );

        String token = magicLink.substring(magicLink.lastIndexOf("/") + 1);

        // when & then
        assertThatThrownBy(() -> guestReservationService.handleGuestAction(token, "invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 액션");
    }
}
