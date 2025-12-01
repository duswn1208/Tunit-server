package com.tunit.domain.user.entity;

import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.dto.StudentProfileSaveDto;
import com.tunit.domain.user.oauth2.OAuth2UserInfo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.STRING)
    private UserProvider provider;
    private String providerId;
    private Boolean isPhoneVerified;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Enumerated(EnumType.STRING)
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

    /**
     * OAuth2 소셜 로그인 사용자 생성 (모든 제공자 공통)
     * @param provider OAuth2 제공자 (NAVER, KAKAO, GOOGLE, APPLE)
     * @param userInfo OAuth2 사용자 정보
     */
    public static UserMain createOAuthUser(UserProvider provider, OAuth2UserInfo userInfo) {
        String userId = provider.name().toLowerCase() + "_" + userInfo.getProviderId();

        return UserMain.of()
                .userId(userId)
                .provider(provider)
                .providerId(userInfo.getProviderId())
                .name(userInfo.getName())
                .nickname(userInfo.getName())
                .phone(userInfo.getPhone())
                .isPhoneVerified(false)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * @deprecated 범용 메서드 createOAuthUser 사용 권장
     */
    @Deprecated
    public static UserMain saveOAuthNaver(String name, String phone, String providerId) {
        String userId = UserProvider.NAVER.name() + "_" + providerId;
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
