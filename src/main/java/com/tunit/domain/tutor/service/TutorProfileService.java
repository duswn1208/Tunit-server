package com.tunit.domain.tutor.service;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.tutor.define.SortType;
import com.tunit.domain.tutor.dto.*;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.entity.TutorRegion;
import com.tunit.domain.tutor.exception.TutorProfileException;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;
    private final UserService userService;

    public TutorProfileResponseDto findTutorProfileInfo(@NonNull Long userNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByUserNo(userNo);

        return TutorProfileResponseDto.from(tutorProfile);
    }

    public TutorProfileResponseDto findTutor(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByTutorProfileNo(tutorProfileNo)
                .orElseThrow();

        return TutorProfileResponseDto.from(tutorProfile,  null, null);
    }

    public TutorProfileResponseDto findTutorProfileInfoByTutorProfileNo(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByTutorProfileNo(tutorProfileNo)
                .orElseThrow();

        List<TutorAvailableTimeResponseDto> availableTimes = tutorAvailableTimeService.findByTutorProfileNo(tutorProfileNo);
        return TutorProfileResponseDto.from(tutorProfile,  availableTimes, null);
    }

    public List<TutorLessonsResponseDto> findTutorLessonsByTutorProfileNo(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(tutorProfileNo).orElseThrow();
        List<TutorLessonsResponseDto> tutorLessons = tutorProfile.getTutorLessons().stream().map(TutorLessonsResponseDto::from).toList();

        return tutorLessons;
    }

    public void checkBusinessAndHolidays(LessonReservation lessonReservation) {
        boolean isAvailable = tutorAvailableTimeService.isWithinAvailableTime(
                lessonReservation.getTutorProfileNo(),
                lessonReservation.getDayOfWeekNum(),
                lessonReservation.getStartTime(),
                lessonReservation.getEndTime()
        );
        if (!isAvailable) {
            throw new LessonNotFoundException("레슨 예약은 영업 시간 내로 가능합니다.");
        }

        boolean isHoliday = tutorHolidayService.isWhithinHoliday(lessonReservation.getTutorProfileNo(), lessonReservation.getDate(), lessonReservation.getStartTime(), lessonReservation.getEndTime());
        if (isHoliday) {
            throw new LessonNotFoundException("레슨 날짜가 휴무일입니다.");
        }
    }

    @Transactional
    public Long save(Long userNo, TutorProfileSaveDto tutorProfileSaveDto) {
        UserMain userMain = userService.findByUserNo(userNo);
        userMain.joinTutor();

        TutorProfile tutorProfile = TutorProfile.saveFrom(userNo, tutorProfileSaveDto);

        TutorProfile save = tutorProfileRepository.save(tutorProfile);

        if (!tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList().isEmpty()) {
            tutorAvailableTimeService.saveAvailableTime(save.getTutorProfileNo(), tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList());
        }

        return save.getTutorProfileNo();
    }

    public TutorProfile findByUserNo(@NonNull Long userNo) {
        return tutorProfileRepository.findByUserNo(userNo);
    }

    public List<TutorProfileResponseDto> findTutors(TutorFindRequestDto tutorFindRequestDto) {
        List<LessonSubCategory> lessonCodes = tutorFindRequestDto.getLessonCodes();
        List<Integer> regionCodes = tutorFindRequestDto.getRegionCodes();
        SortType sortType = tutorFindRequestDto.getSortType();

        if (lessonCodes != null && lessonCodes.isEmpty()) lessonCodes = null;

        List<Long> tutorProfileNos = tutorProfileRepository.findTutorProfileNoByCategory(lessonCodes);
        if (tutorProfileNos.isEmpty()) {
            return List.of();
        }

        List<TutorProfile> profileList = tutorProfileRepository.findByTutorProfileNoIn(tutorProfileNos);

        // 지역 코드 필터링 (애플리케이션 레벨)
        if (regionCodes != null && !regionCodes.isEmpty()) {
            profileList = profileList.stream()
                    .filter(tutorProfile -> {
                        // 튜터의 지역 코드 리스트
                        List<Integer> tutorRegionCodes = tutorProfile.getTutorRegions().stream()
                                .map(TutorRegion::getCode)
                                .toList();

                        // 요청한 지역 코드 중 하나라도 매칭되면 true
                        return regionCodes.stream().anyMatch(requestedCode -> {
                            String requestedCodeStr = String.valueOf(requestedCode);
                            int codeLength = requestedCodeStr.length();

                            // 2자리 코드 (예: 11, 41) → prefix 매칭
                            if (codeLength == 2) {
                                return tutorRegionCodes.stream()
                                        .anyMatch(tutorCode -> String.valueOf(tutorCode).startsWith(requestedCodeStr));
                            }
                            // 그 외 → 정확히 일치
                            else {
                                return tutorRegionCodes.contains(requestedCode);
                            }
                        });
                    })
                    .toList();
        }

        List<TutorProfileResponseDto> result = profileList.stream().map(TutorProfileResponseDto::from).toList();

        if (sortType != null) {
            switch (sortType) {
                case LATEST -> result = result.stream()
                        .sorted((a, b) -> b.tutorProfileNo().compareTo(a.tutorProfileNo())) // 최신 등록순(내림차순)
                        .toList();
                case PRICE_LOW -> result = result.stream()
                        .sorted((a, b) -> Integer.compare(a.pricePerHour(), b.pricePerHour()))
                        .toList();
                case PRICE_HIGH -> result = result.stream()
                        .sorted((a, b) -> Integer.compare(b.pricePerHour(), a.pricePerHour()))
                        .toList();
                // REVIEW(후기 많은 순)는 추후 구현
            }
        }
        return result;
    }

    @Transactional
    public void modifyTutorAvailableTime(Long tutorProfileNo, TutorAvailableTimeUpdateDto tutorAvailableTimeUpdateDto) {
        List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList = tutorAvailableTimeUpdateDto.getTutorAvailableTimeSaveDtoList();
        if (tutorAvailableTimeSaveDtoList.isEmpty()) {
            throw new TutorProfileException("수정할 시간표가 없습니다.");
        }

        tutorAvailableTimeService.deleteAllByTutorProfileNo(tutorProfileNo);
        List<TutorAvailableTime> tutorAvailableTimes = tutorAvailableTimeService.saveAvailableTime(tutorProfileNo, tutorAvailableTimeSaveDtoList);

        if (tutorAvailableTimeSaveDtoList.size() != tutorAvailableTimes.size()) {
            log.warn("튜터 프로필 번호 {}의 수업 가능 시간 저장 건수 불일치: 요청 {}건, 저장 {}건", tutorProfileNo, tutorAvailableTimeSaveDtoList.size(), tutorAvailableTimes.size());
            throw new TutorProfileException();
        }

        log.info("튜터 프로필 번호 {}의 수업 가능 시간 {}건이 모두 정상적으로 저장되었습니다.", tutorProfileNo, tutorAvailableTimes.size());
    }
}
