package com.tunit.common.session.dto;

import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.entity.UserMain;

public class SessionUser {

    private Long userNo;
    private UserRole userRole;
    private String userId;
    private String name;
    private UserStatus userStatus;
    private Long tutorProfileNo;

    public SessionUser(UserMain userMain, Long tutorProfileNo) {
        this.userNo = userMain.getUserNo();
        this.userRole = userMain.getUserRole();
        this.userId = userMain.getUserId();
        this.name = userMain.getName();
        this.userStatus = userMain.getUserStatus();
        this.tutorProfileNo = tutorProfileNo;
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
