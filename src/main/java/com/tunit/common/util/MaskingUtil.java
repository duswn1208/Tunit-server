package com.tunit.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MaskingUtil {

    /**
     * 이름을 마스킹 (첫 글자와 마지막 글자만 표시, 중간은 *)
     */
    public static String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    /**
     * 아이디를 마스킹 (앞 2글자, 뒤 2글자 표시, 중간은 *)
     */
    public static String maskId(String id) {
        if (id == null || id.length() <= 3) {
            return "*".repeat(id.length());
        }
        return id.substring(0, 2) + "*".repeat(id.length() - 4) + id.substring(id.length() - 2);
    }

}
