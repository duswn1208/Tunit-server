package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.LessonPeriodType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public record LessonFindSummaryDto(
        Integer todayLessonCount,
        Integer thisWeekLessonCount,
        Integer nextWeekLessonCount,
        Integer thisMonthLessonCount,
        Integer totalLessonCount,
        List<LessonResponsDto> lessonList
) {

    public static LessonFindSummaryDto from(List<LessonResponsDto> lessonList) {
        int todayCount = countByPeriod(lessonList, LessonPeriodType.TODAY);
        int thisWeekAfterTodayCount = countByPeriod(lessonList, LessonPeriodType.THIS_WEEK_AFTER_TODAY);
        int nextWeekCount = countByPeriod(lessonList, LessonPeriodType.NEXT_WEEK);
        int thisMonthCount = countByPeriod(lessonList, LessonPeriodType.THIS_MONTH);
        int totalCount = lessonList.size();
        return new LessonFindSummaryDto(
                todayCount,
                thisWeekAfterTodayCount,
                nextWeekCount,
                thisMonthCount,
                totalCount,
                lessonList
        );
    }

    public static int countByPeriod(List<LessonResponsDto> lessonList, LessonPeriodType type) {
        LocalDate now = LocalDate.now();
        return (int) lessonList.stream()
                .filter(lesson -> isInPeriod(lesson.date(), now, type))
                .count();
    }

    private static boolean isInPeriod(LocalDate date, LocalDate now, LessonPeriodType type) {
        if (date == null) return false;
        switch (type) {
            case TODAY:
                return date.isEqual(now);
            case THIS_WEEK:
                LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
                return !date.isBefore(now) && !date.isAfter(endOfWeek);
            case THIS_WEEK_AFTER_TODAY:
                LocalDate endOfThisWeek = now.with(DayOfWeek.SUNDAY);
                return date.isAfter(now) && !date.isAfter(endOfThisWeek);
            case NEXT_WEEK:
                LocalDate startOfNextWeek = now.with(DayOfWeek.MONDAY).plusWeeks(1);
                LocalDate endOfNextWeek = startOfNextWeek.with(DayOfWeek.SUNDAY);
                return !date.isBefore(startOfNextWeek) && !date.isAfter(endOfNextWeek);
            case THIS_MONTH:
                LocalDate startOfMonth = now.withDayOfMonth(1);
                LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
                return !date.isBefore(startOfMonth) && !date.isAfter(endOfMonth);
            default:
                return false;
        }
    }
}
