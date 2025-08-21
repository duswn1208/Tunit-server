package com.tunit.domain.user.define;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    TUTOR,
    STUDENT,
    ADMIN,
    ;

    public boolean isTutor() {
        return this == TUTOR;
    }
    public boolean isStudent() {
        return this == STUDENT;
    }
    public boolean isAdmin() {
        return this == ADMIN;
    }

}
