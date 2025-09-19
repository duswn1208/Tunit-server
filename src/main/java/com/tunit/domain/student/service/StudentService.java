package com.tunit.domain.student.service;

import com.tunit.domain.student.dto.StudentInfoResponseDto;
import com.tunit.domain.student.entity.StudentLessons;
import com.tunit.domain.student.entity.StudentRegions;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.dto.StudentProfileSaveDto;
import com.tunit.domain.user.dto.UserMainResponseDto;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.repository.UserRepository;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final StudentLessonsService studentLessonsService;
    private final StudentRegionsService studentRegionsService;

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
        Set<StudentLessons> lessons = studentLessonsService.findByUserNo(userNo);
        Set<StudentRegions> regions = studentRegionsService.findByUserNo(userNo);

        return StudentInfoResponseDto.from(userNo, lessons, regions);
    }
}
