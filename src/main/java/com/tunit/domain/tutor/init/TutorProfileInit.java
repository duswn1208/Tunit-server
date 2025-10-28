package com.tunit.domain.tutor.init;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.entity.*;
import com.tunit.domain.tutor.repository.TutorAvailExceptionRepository;
import com.tunit.domain.tutor.repository.TutorAvailableTimeRepository;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TutorProfileInit {
    private final TutorProfileRepository tutorProfileRepository;
    private final TutorAvailableTimeRepository tutorAvailableTimeRepository;
    private final TutorAvailExceptionRepository tutorAvailExceptionRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        RegionSaveDto seoul = new RegionSaveDto(11, "서울특별시", "sido", 11, "서울특별시 전체");
        RegionSaveDto geongi = new RegionSaveDto(41, "경기도", "sido", 41, "경기도 전체");
        log.info("TutorProfileInit - init");

        // UserMain 생성 및 저장 (userNo 자동 할당)
        UserMain user1 = UserMain.of().name("튜터1").nickname("튜터닉1").build();
        UserMain user2 = UserMain.of().name("튜터2").nickname("튜터닉2").build();
        UserMain user3 = UserMain.of().name("튜터3").nickname("튜터닉3").build();
        UserMain user4 = UserMain.of().name("튜터4").nickname("튜터닉4").build();
        UserMain user5 = UserMain.of().name("튜터5").nickname("튜터닉5").build();
        List<UserMain> savedUsers = userRepository.saveAll(List.of(user1, user2, user3, user4, user5));

        TutorProfile tutor1 = TutorProfile.of()
                .userNo(savedUsers.get(0).getUserNo())
                .introduce("악기 전문 튜터입니다.")
                .careerYears(5)
                .pricePerHour(30000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        tutor1.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor1, LessonSubCategory.GUITAR), TutorLessons.saveFrom(tutor1, LessonSubCategory.BASE)));
        tutor1.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor1, seoul), TutorRegion.saveFrom(tutor1, geongi)));

        TutorProfile tutor2 = TutorProfile.of()
                .userNo(savedUsers.get(1).getUserNo())
                .introduce("운동 전문 튜터입니다.")
                .careerYears(3)
                .pricePerHour(25000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        tutor2.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor2, LessonSubCategory.PILATES), TutorLessons.saveFrom(tutor2, LessonSubCategory.YOGA)));
        tutor2.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor2, seoul), TutorRegion.saveFrom(tutor2, geongi)));

        TutorProfile tutor3 = TutorProfile.of()
                .userNo(savedUsers.get(2).getUserNo())
                .introduce("필라테스 전문 튜터입니다.")
                .careerYears(7)
                .pricePerHour(35000)
                .durationMin(90)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        tutor3.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor3, LessonSubCategory.PILATES), TutorLessons.saveFrom(tutor3, LessonSubCategory.YOGA)));
        tutor3.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor3, seoul), TutorRegion.saveFrom(tutor3, geongi)));

        TutorProfile tutor4 = TutorProfile.of()
                .userNo(savedUsers.get(3).getUserNo())
                .introduce("교사 전문 튜터입니다.")
                .careerYears(4)
                .pricePerHour(28000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        tutor4.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor4, LessonSubCategory.KOREAN), TutorLessons.saveFrom(tutor4, LessonSubCategory.ENGLISH)));
        tutor4.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor4, seoul), TutorRegion.saveFrom(tutor4, geongi)));

        TutorProfile tutor5 = TutorProfile.of()
                .userNo(savedUsers.get(4).getUserNo())
                .introduce("요리 전문 튜터입니다.")
                .careerYears(6)
                .pricePerHour(32000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        tutor5.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor5, LessonSubCategory.BAKING), TutorLessons.saveFrom(tutor5, LessonSubCategory.COOKING)));
        tutor5.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor5, seoul), TutorRegion.saveFrom(tutor5, geongi)));

        tutorProfileRepository.saveAll(List.of(tutor1, tutor2, tutor3, tutor4, tutor5));

        // 저장된 TutorProfile의 PK(tutorProfileNo)로 TutorAvailableTime 별도 저장
        List<TutorProfile> savedProfiles = tutorProfileRepository.findAll();
        log.info("savedProfiles.size(): {}", savedProfiles.size());
        for (TutorProfile profile : savedProfiles) {
            log.info("profile.getTutorProfileNo: {}", profile.getTutorProfileNo());

            if (profile.getTutorProfileNo() == 1L) {
                tutorAvailableTimeRepository.saveAll(Set.of(
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(1).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(19, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(3).startTime(LocalTime.of(12, 0)).endTime(LocalTime.of(16, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(4).startTime(LocalTime.of(12, 0)).endTime(LocalTime.of(16, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(5).startTime(LocalTime.of(12, 0)).endTime(LocalTime.of(16, 0)).build()
                ));
            } else if (profile.getTutorProfileNo() == 2L) {
                tutorAvailableTimeRepository.saveAll(Set.of(
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(2).startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(11, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(4).startTime(LocalTime.of(15, 0)).endTime(LocalTime.of(17, 0)).build()
                ));
            } else if (profile.getTutorProfileNo() == 3L) {
                tutorAvailableTimeRepository.saveAll(Set.of(
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(5).startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(6).startTime(LocalTime.of(13, 0)).endTime(LocalTime.of(15, 0)).build()
                ));
            }

            // 튜터별 예외일정 등록
            if (profile.getTutorProfileNo() == 1L) {
                tutorAvailExceptionRepository.saveAll(Set.of(
                        TutorAvailException.of()
                                .tutorProfileNo(profile.getTutorProfileNo())
                                .date(LocalDate.of(2025, 10, 15))
                                .startTime(LocalTime.of(0, 0))
                                .endTime(LocalTime.of(23, 59))
                                .type(TutorLessonOpenType.BLOCK)
                                .reason("개인 사정")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build(),
                        TutorAvailException.of()
                                .tutorProfileNo(profile.getTutorProfileNo())
                                .date(LocalDate.of(2025, 10, 21))
                                .startTime(LocalTime.of(0, 0))
                                .endTime(LocalTime.of(23, 59))
                                .type(TutorLessonOpenType.BLOCK)
                                .reason("휴가")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));
            } else if (profile.getTutorProfileNo() == 2L) {
                tutorAvailExceptionRepository.saveAll(Set.of(
                        TutorAvailException.of()
                                .tutorProfileNo(profile.getTutorProfileNo())
                                .date(LocalDate.of(2025, 10, 16))
                                .startTime(LocalTime.of(0, 0))
                                .endTime(LocalTime.of(23, 59))
                                .type(TutorLessonOpenType.BLOCK)
                                .reason("세미나 참석")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));
            } else if (profile.getTutorProfileNo() == 3L) {
                tutorAvailExceptionRepository.saveAll(Set.of(
                        TutorAvailException.of()
                                .tutorProfileNo(profile.getTutorProfileNo())
                                .date(LocalDate.of(2025, 10, 18))
                                .startTime(LocalTime.of(0, 0))
                                .endTime(LocalTime.of(23, 59))
                                .type(TutorLessonOpenType.BLOCK)
                                .reason("출장")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build(),
                        TutorAvailException.of()
                                .tutorProfileNo(profile.getTutorProfileNo())
                                .date(LocalDate.of(2025, 10, 25))
                                .startTime(LocalTime.of(0, 0))
                                .endTime(LocalTime.of(23, 59))
                                .type(TutorLessonOpenType.BLOCK)
                                .reason("연차")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));
            }
        }
    }
}
