package com.tunit.domain.student.service;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.review.service.LessonReviewService;
import com.tunit.domain.student.dto.FindMyLessonsRequestDto;
import com.tunit.domain.student.dto.StudentInfoResponseDto;
import com.tunit.domain.student.dto.StudentLessonResponseDto;
import com.tunit.domain.student.entity.StudentLessons;
import com.tunit.domain.student.entity.StudentRegions;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
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
    private final LessonReviewService lessonReviewService;

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
    public List<StudentLessonResponseDto> findMyLessons(Long userNo, FindMyLessonsRequestDto requestDto) {
        requestDto.setDefaultValuesIfNull();

        List<LessonReservation> lessons = lessonReservationRepository.findByStudentNoAndOptionalContractNoAndDateBetween(
                userNo, requestDto.getContractNo(), requestDto.getStartDate(), requestDto.getEndDate());

        List<ReservationStatus> statuses = requestDto.getLessonFilter().getIncludedStatuses();
        if (statuses != null && !statuses.isEmpty()) {
            lessons = lessons.stream().filter(l -> statuses.contains(l.getStatus())).toList();
        }
        if (requestDto.getLessonCategory() != null) {
            lessons = lessons.stream().filter(l -> false).toList();
        }

        return lessons.stream()
                .map(lesson -> {
                    TutorProfileResponseDto tutorProfileInfoByTutorProfileNo = tutorProfileService.findTutor(lesson.getTutorProfileNo());
                    UserMainResponseDto tutorProfile = userService.findDtoByUserNo(tutorProfileInfoByTutorProfileNo.userNo());

                    boolean isReviewed = lessonReviewService.existsByLessonReservationNo(lesson.getLessonReservationNo());


                    return StudentLessonResponseDto.of(lesson, tutorProfile, isReviewed);
                })
                .collect(Collectors.toList());
    }
}
