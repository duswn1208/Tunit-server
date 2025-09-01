package com.tunit.common.util;

import java.util.Map;
import java.util.Set;

public class KoreanDayOfWeekUtil {
    public static final Set<String> DAY_OF_WEEK_SET = Set.of("월", "화", "수", "목", "금", "토", "일");
    private static final Map<String, Integer> KOR_DAY_TO_NUM = Map.of(
        "월", 1,
        "화", 2,
        "수", 3,
        "목", 4,
        "금", 5,
        "토", 6,
        "일", 7
    );

    public static int getDayOfWeekNum(String korDay) {
        if (korDay == null) throw new IllegalArgumentException("요일 값이 null입니다.");
        Integer num = KOR_DAY_TO_NUM.get(korDay.trim());
        if (num == null) throw new IllegalArgumentException("잘못된 요일: " + korDay);
        return num;
    }
}

