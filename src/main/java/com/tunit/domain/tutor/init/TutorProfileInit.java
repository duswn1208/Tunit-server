package com.tunit.domain.tutor.init;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.entity.TutorRegion;
import com.tunit.domain.tutor.repository.TutorAvailableTimeRepository;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    @PostConstruct
    public void init() {
        RegionSaveDto seoul = new RegionSaveDto(11, "서울특별시", "sido", 11, "서울특별시 전체");
        RegionSaveDto geongi = new RegionSaveDto(41, "경기도", "sido", 41, "경기도 전체");
        log.info("TutorProfileInit - init");

        TutorProfile tutor1 = TutorProfile.of()
                .userNo(1L)
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
                .userNo(2L)
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
                .userNo(3L)
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
                .userNo(4L)
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
                .userNo(5L)
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
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(1).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(3).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0)).build()
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
            } else if (profile.getTutorProfileNo() == 4L) {
                tutorAvailableTimeRepository.saveAll(Set.of(
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(1).startTime(LocalTime.of(17, 0)).endTime(LocalTime.of(19, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(7).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0)).build()
                ));
            } else if (profile.getTutorProfileNo() == 5L) {
                tutorAvailableTimeRepository.saveAll(Set.of(
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(2).startTime(LocalTime.of(18, 0)).endTime(LocalTime.of(20, 0)).build(),
                        TutorAvailableTime.of().tutorProfileNo(profile.getTutorProfileNo()).dayOfWeekNum(4).startTime(LocalTime.of(11, 0)).endTime(LocalTime.of(13, 0)).build()
                ));
            }
        }
    }
}
