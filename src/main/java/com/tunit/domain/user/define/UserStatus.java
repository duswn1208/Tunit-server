package com.tunit.domain.user.define;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    SUSPENDED("정지"),
    DELETED("삭제");

    private final String description;
}
