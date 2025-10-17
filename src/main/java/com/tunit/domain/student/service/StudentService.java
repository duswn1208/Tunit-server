package com.tunit.domain.student.service;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.student.dto.StudentInfoResponseDto;
import com.tunit.domain.student.dto.StudentLessonResponseDto;
import com.tunit.domain.student.entity.StudentLessons;
import com.tunit.domain.student.entity.StudentRegions;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.dto.StudentProfileSaveDto;
import com.tunit.domain.user.dto.UserMainResponseDto;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.repository.UserRepository;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final StudentLessonsService studentLessonsService;
    private final StudentRegionsService studentRegionsService;
    private final LessonReservationRepository lessonReservationRepository;
    private final TutorProfileService tutorProfileService;

    public void joinStudentProfile(StudentProfileSaveDto studentProfileSaveDto) {

        UserMain userMain = userService.findByUserNo(studentProfileSaveDto.getUserNo());
        userMain.updateStudentProfile(studentProfileSaveDto);

        // lesson, region save
        studentLessonsService.saveAll(
            studentProfileSaveDto.getSubCategoryList().stream()
                .map(lesson -> StudentLessons.saveFrom(studentProfileSaveDto.getUserNo(), lesson))
                .toList()
        );
        studentRegionsService.saveAll(
            studentProfileSaveDto.getRegionList().stream()
                .map(region -> StudentRegions.saveFrom(studentProfileSaveDto.getUserNo(), region))
                .toList()
        );

        userRepository.save(userMain);
    }

    public boolean needOnboardingStudent(Long userNo) {
        UserMain userMain = userRepository.findById(userNo).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. userNo=" + userNo));

        return userMain.getUserStatus().equals(UserStatus.WAITING);
    }

    public StudentInfoResponseDto findStudentByUserNo(Long userNo) {
        Set<LessonSubCategory> lessons = studentLessonsService.findByUserNo(userNo).stream().map(StudentLessons::getLessonSubCategory).collect(Collectors.toSet());
        Set<StudentRegions> regions = studentRegionsService.findByUserNo(userNo);

        return StudentInfoResponseDto.from(userNo, lessons, regions);
    }

    @Transactional(readOnly = true)
    public List<StudentLessonResponseDto> findMyLessons(Long userNo, LocalDate startDate, LocalDate endDate) {
        // 시작일이 없으면 현재 달의 시작일로 설정
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        // 종료일이 없으면 현재 달의 마지막일로 설정
        if (endDate == null) {
            endDate = startDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
        }

        // 레슨 예약 정보 조회
        List<LessonReservation> lessons = lessonReservationRepository.findByStudentNoAndDateBetweenOrderByDateAscStartTimeAsc(
                userNo, startDate, endDate);

        // 튜터 정보와 함께 응답 DTO 생성
        return lessons.stream()
                .map(lesson -> {
                    String tutorName = userService.findNameByUserNo(
                            tutorProfileService.findTutorProfileInfoByTutorProfileNo(lesson.getTutorProfileNo()).userNo()
                    );
                    return StudentLessonResponseDto.of(lesson, tutorName);
                })
                .collect(Collectors.toList());
    }
}
