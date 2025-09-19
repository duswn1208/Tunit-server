package com.tunit.domain.user.dto;

import com.tunit.domain.student.dto.StudentInfoResponseDto;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.entity.UserMain;

import java.time.LocalDateTime;

public record  UserMainResponseDto  (
        Long userNo,
        String userId,
        String name,
        String nickname,
        String phone,
        Boolean isPhoneVerified,
        UserStatus userStatus,
        UserRole userRole,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        TutorProfileResponseDto tutorProfile,
        StudentInfoResponseDto studentInfo
){

    public static UserMainResponseDto tutorFrom(UserMain userMain, TutorProfileResponseDto tutorProfileResponseDto) {
        return new UserMainResponseDto(
                userMain.getUserNo(),
                userMain.getUserId(),
                userMain.getName(),
                userMain.getNickname(),
                userMain.getPhone(),
                userMain.getIsPhoneVerified(),
                userMain.getUserStatus(),
                userMain.getUserRole(),
                userMain.getCreatedAt(),
                userMain.getUpdatedAt(),
               tutorProfileResponseDto,
                null
        );
    }

    public static UserMainResponseDto studentFrom(UserMain userMain, StudentInfoResponseDto studentInfoResponseDto) {
        return new UserMainResponseDto(
                userMain.getUserNo(),
                userMain.getUserId(),
                userMain.getName(),
                userMain.getNickname(),
                userMain.getPhone(),
                userMain.getIsPhoneVerified(),
                userMain.getUserStatus(),
                userMain.getUserRole(),
                userMain.getCreatedAt(),
                userMain.getUpdatedAt(),
                null,
               studentInfoResponseDto
        );
    }
}
