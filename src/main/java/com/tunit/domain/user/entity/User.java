package com.tunit.domain.user.entity;

import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userNo;
    private String userId;
    private String name;
    private String phone;
    private UserProvider provider;
    private String providerId;
    private Boolean isPhoneVerified;
    private UserStatus userStatus;
    private UserRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")

    public User(Integer userNo, String userId, String name, String phone,
                UserProvider provider, String providerId, Boolean isPhoneVerified, UserStatus userStatus, UserRole userRole, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userNo = userNo;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.provider = provider;
        this.providerId = providerId;
        this.isPhoneVerified = isPhoneVerified;
        this.userStatus = userStatus;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User saveOAuthNaver(String name, String phone, String providerId) {
        String userId = UserProvider.NAVER.name() + "_" + providerId; // e.g., "naver_1234567890"
        return User.of()
                .userId(userId)
                .provider(UserProvider.NAVER)
                .providerId(providerId)
                .name(name)
                .phone(phone)
                .isPhoneVerified(false)
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.TUTOR)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static User findFrom(UserProvider provider, String providerId) {
        return User.of()
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
