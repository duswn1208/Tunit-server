package com.tunit.domain.user.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserRole {
    TUTOR("튜터"),
    STUDENT("학생"),
    ADMIN("관리자"),
    ;

    private final String label;

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
