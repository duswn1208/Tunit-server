package com.tunit.common.session.dto;

import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.entity.UserMain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

public record SessionUser (
        Long userNo,
        UserRole userRole,
        String userId,
        String name,
        UserStatus userStatus,
        Long tutorProfileNo
) {

    public static SessionUser create(UserMain userMain) {
        return new SessionUser(userMain.getUserNo(), userMain.getUserRole(), userMain.getUserId(), userMain.getName(), userMain.getUserStatus(), null);
    }

    public static SessionUser create(UserMain userMain, Long tutorProfileNo) {
        return new SessionUser(userMain.getUserNo(), userMain.getUserRole(), userMain.getUserId(), userMain.getName(), userMain.getUserStatus(), tutorProfileNo);
    }

    public boolean isTutor() {
        return userRole.isTutor();
    }
    public boolean isStudent() {
        return userRole.isStudent();
    }
    public boolean isAdmin() {
        return userRole.isAdmin();
    }
}
