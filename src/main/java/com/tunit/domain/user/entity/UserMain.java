package com.tunit.domain.user.entity;

import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.dto.StudentProfileSaveDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class UserMain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;
    private String userId;
    private String name;
    private String nickname;
    private String phone;
    private UserProvider provider;
    private String providerId;
    private Boolean isPhoneVerified;
    private UserStatus userStatus;
    private UserRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public UserMain(Long userNo, String userId, String name, String nickname, String phone,
                    UserProvider provider, String providerId, Boolean isPhoneVerified, UserStatus userStatus, UserRole userRole,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userNo = userNo;
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.provider = provider;
        this.providerId = providerId;
        this.isPhoneVerified = isPhoneVerified;
        this.userStatus = userStatus;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserMain saveOAuthNaver(String name, String phone, String providerId) {
        String userId = UserProvider.NAVER.name() + "_" + providerId; // e.g., "naver_1234567890"
        return UserMain.of()
                .userId(userId)
                .provider(UserProvider.NAVER)
                .providerId(providerId)
                .name(name)
                .nickname(name)
                .phone(phone)
                .isPhoneVerified(false)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static UserMain findFrom(UserProvider provider, String providerId) {
        return UserMain.of()
                .provider(provider)
                .providerId(providerId)
                .build();
    }

    public static UserMain saveWaitingStudent(String name, String phone) {
        return UserMain.of()
                .userId("student_" + phone) // e.g., "student_01012345678"
                .name(name)
                .nickname(name)
                .phone(phone)
                .isPhoneVerified(false)
                .userStatus(UserStatus.WAITING)
                .userRole(UserRole.STUDENT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void joinStudent() {
        this.userStatus = UserStatus.ACTIVE;
        this.userRole = UserRole.STUDENT;
        this.updatedAt = LocalDateTime.now();
//        this.nickname = "S";
    }

    public void joinTutor(String nickName) {
        this.nickname = nickName;
        this.userStatus = UserStatus.ACTIVE;
        this.userRole = UserRole.TUTOR;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStudentProfile(StudentProfileSaveDto studentProfileSaveDto) {
        this.nickname = studentProfileSaveDto.getNickname();
        this.userRole = UserRole.STUDENT;
        this.userStatus = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
}
