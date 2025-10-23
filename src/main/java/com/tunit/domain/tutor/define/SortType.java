package com.tunit.domain.tutor.define;

import lombok.Getter;

@Getter
public enum SortType {
    REVIEW("REVIEW", "후기 많은 순"),
    LATEST("LATEST", "최신 등록순"),
    PRICE_LOW("PRICE_LOW", "가격 낮은 순"),
    PRICE_HIGH("PRICE_HIGH", "가격 높은 순");

    private final String code;
    private final String label;

    SortType(String code, String label) {
        this.code = code;
        this.label = label;
    }

}
