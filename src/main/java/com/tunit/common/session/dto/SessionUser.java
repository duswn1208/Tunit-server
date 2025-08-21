package com.tunit.common.session.dto;

import com.tunit.domain.user.define.UserRole;
import com.tunit.domain.user.define.UserStatus;
import com.tunit.domain.user.entity.User;

public class SessionUser {

    private Long userNo;
    private UserRole userRole;
    private String userId;
    private String name;
    private UserStatus userStatus;
    private Long tutorProfileNo;

    public SessionUser(User user, Long tutorProfileNo) {
        this.userNo = user.getUserNo();
        this.userRole = user.getUserRole();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.userStatus = user.getUserStatus();
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
