package com.tunit.domain.lesson.service;

import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.repository.ContractScheduleRepository;
import com.tunit.domain.contract.repository.StudentTutorContractRepository;
import com.tunit.domain.lesson.define.ReservationSource;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.GuestReservationRequestDto;
import com.tunit.domain.lesson.dto.GuestReservationResponseDto;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 비회원 예약 서비스
 */
@Service
@RequiredArgsConstructor
public class GuestReservationService {

    private final UserRepository userRepository;
    private final TutorProfileRepository tutorProfileRepository;
    private final LessonReservationRepository lessonReservationRepository;
    private final StudentTutorContractRepository contractRepository;
    
    @Value("${service-url.web:http://localhost:5173}")
    private String webUrl;
    
    private final Key jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * 비회원 예약 생성 (체험레슨 고정)
     */
    @Transactional
    public String createGuestReservation(Long tutorProfileNo, GuestReservationRequestDto dto) {
        // 0. TRIAL 타입 보장
        dto = dto.ensureTrialType();
        
        // 1. 선생님 프로필 조회
        TutorProfile tutorProfile = tutorProfileRepository.findById(tutorProfileNo)
                .orElseThrow(() -> new IllegalArgumentException("선생님 프로필을 찾을 수 없습니다."));

        // 2. 임시 학생 생성 또는 조회 (전화번호 기준)
        GuestReservationRequestDto finalDto = dto;
        UserMain student = userRepository.findByPhone(dto.getPhone())
                .orElseGet(() -> {
                    UserMain newStudent = UserMain.saveWaitingStudent(finalDto.getStudentName(), finalDto.getPhone());
                    return userRepository.save(newStudent);
                });

        // 3. 체험레슨 계약 생성
        LocalDate lessonDate = dto.getLessonDate();
        
        StudentTutorContract contract = StudentTutorContract.builder()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(student.getUserNo())
                .startDt(lessonDate)
                .endDt(lessonDate)  // 체험레슨은 1회성
                .contractStatus(ContractStatus.REQUESTED)
                .contractType(dto.getContractType())  // TRIAL 고정
                .lessonSubCategory(dto.getLessonCategory())
                .lessonCount(1)
                .lessonName(dto.getLessonCategory().getLabel() + " 체험레슨")
                .level(dto.getLevel())
                .place(dto.getPlace())
                .totalPrice(dto.getTotalPrice())
                .memo(dto.getMemo())
                .source(ContractSource.STUDENT_REQUEST)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        StudentTutorContract savedContract = contractRepository.save(contract);

        // 4. 첫 예약 생성
        LessonReservation reservation = LessonReservation.of()
                .tutorProfileNo(tutorProfileNo)
                .studentNo(student.getUserNo())
                .contractNo(savedContract.getContractNo())
                .lessonCategory(dto.getLessonCategory())
                .date(dto.getLessonDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getStartTime().plusMinutes(tutorProfile.getDurationMin()))
                .dayOfWeekNum(dto.getLessonDate().getDayOfWeek().getValue())
                .status(ReservationStatus.REQUESTED)
                .source(ReservationSource.GUEST)
                .memo(dto.getMemo())
                .build();

        LessonReservation savedReservation = lessonReservationRepository.save(reservation);

        // 5. 마법 링크 생성
        String token = generateMagicToken(dto.getPhone(), savedReservation.getLessonReservationNo());
        String magicLink = webUrl + "/reservations/verify/" + token;

        // 6. TODO: 카카오 알림톡 발송
        // sendKakaoNotification(dto.getPhone(), magicLink, tutorProfile, dto.getLessonDate(), dto.getStartTime());

        return magicLink;
    }

    /**
     * 마법 링크 토큰으로 예약 조회
     */
    @Transactional(readOnly = true)
    public GuestReservationResponseDto getReservationByToken(String token) {
        Claims claims = validateToken(token);
        Long reservationNo = claims.get("reservationNo", Long.class);

        LessonReservation reservation = lessonReservationRepository.findById(reservationNo)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        UserMain student = userRepository.findById(reservation.getStudentNo())
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

        TutorProfile tutorProfile = tutorProfileRepository.findById(reservation.getTutorProfileNo())
                .orElseThrow(() -> new IllegalArgumentException("선생님 정보를 찾을 수 없습니다."));
        
        UserMain tutor = userRepository.findById(tutorProfile.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("선생님 사용자 정보를 찾을 수 없습니다."));

        return new GuestReservationResponseDto(
                reservation.getLessonReservationNo(),
                student.getName(),
                tutor.getNickname(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getLessonCategory().name(),
                reservation.getStatus(),
                reservation.getMemo()
        );
    }

    /**
     * 예약 승인/거절 처리 (학생용)
     */
    @Transactional
    public void handleGuestAction(String token, String action) {
        Claims claims = validateToken(token);
        Long reservationNo = claims.get("reservationNo", Long.class);

        LessonReservation reservation = lessonReservationRepository.findById(reservationNo)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if ("confirm".equals(action)) {
            reservation.changeStatus(ReservationStatus.REQUESTED, ReservationStatus.ACTIVE);
        } else if ("reject".equals(action)) {
            reservation.changeStatus(ReservationStatus.REQUESTED, ReservationStatus.CANCELED);
        } else {
            throw new IllegalArgumentException("잘못된 액션입니다: " + action);
        }

        lessonReservationRepository.save(reservation);
    }

    /**
     * 마법 링크용 JWT 토큰 생성
     */
    private String generateMagicToken(String phone, Long reservationNo) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        
        return Jwts.builder()
                .subject(phone)
                .claim("reservationNo", reservationNo)
                .issuedAt(new Date())
                .expiration(Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(jwtKey)
                .compact();
    }

    /**
     * 토큰 검증
     */
    private Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) jwtKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.", e);
        }
    }
}
