package com.tunit.domain.contract.util;

import com.tunit.common.util.ExcelParser;
import com.tunit.common.util.KoreanDayOfWeekUtil;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.dto.ContractExcelSaveDto;
import com.tunit.domain.contract.dto.ContractScheduleDto;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
public class ContractExcelRowParser {

    /**
     * 계약 정보를 포함한 엑셀 파싱
     *
     * 요일/시간 입력 방식:
     * 1. 모든 요일 같은 시간: day_of_week="월,수,금", start_time="14:00"
     * 2. 요일별 다른 시간: schedules="월(14:00-15:00),수(18:00-19:00),금(20:00-21:00)"
     */
    public List<ContractExcelSaveDto> parseWithContract(MultipartFile file) {
        List<ContractExcelSaveDto> rows = new ArrayList<>();
        Map<String, Integer> headerMap = new HashMap<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return rows;

            // 헤더 매핑 (동의어 지원)
            Map<String, String> columnAlias = Map.of(
                "unit_price", "total_price",
                "week_count", "lesson_count"
            );
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = ExcelParser.getCellString(headerRow.getCell(i)).trim().toLowerCase();
                headerMap.put(header, i);
                // 동의어도 매핑
                for (Map.Entry<String, String> entry : columnAlias.entrySet()) {
                    if (header.equals(entry.getValue())) {
                        headerMap.put(entry.getKey(), i);
                    }
                }
            }

            // 데이터 행 파싱
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isRowEmpty(row)) continue;

                String name = getCellStringSafe(row, headerMap, "name");
                String phone = getCellStringSafe(row, headerMap, "phone");
                String lesson = getCellStringSafe(row, headerMap, "lesson");
                LessonSubCategory lessonSubCategory = LessonSubCategory.fromLabel(lesson);
                String firstLessonDateStr = getCellStringSafe(row, headerMap, "first_lesson_date");
                LocalDate firstLessonDate = firstLessonDateStr != null && !firstLessonDateStr.isBlank() ? LocalDate.parse(firstLessonDateStr) : null;
                String memo = getCellStringSafe(row, headerMap, "memo");

                List<ContractScheduleDto> schedules;
                String schedulesStr = getCellStringSafe(row, headerMap, "schedules");

                if (schedulesStr != null && !schedulesStr.isBlank()) {
                    // 방식 2: schedules 컬럼 사용 - "월(14:00-15:00),수(18:00-19:00)"
                    schedules = parseSchedulesString(schedulesStr);
                } else {
                    // 방식 1: 기존 방식 - day_of_week + start_time (모든 요일 같은 시간)
                    String dayOfWeekString = getCellStringSafe(row, headerMap, "day_of_week");
                    String startTimeStr = getCellStringSafe(row, headerMap, "start_time");
                    LocalTime startTime = startTimeStr != null && !startTimeStr.isBlank() ? LocalTime.parse(startTimeStr) : null;
                    String endTimeStr = getCellStringSafe(row, headerMap, "end_time");
                    LocalTime endTime = endTimeStr != null && !endTimeStr.isBlank()
                            ? LocalTime.parse(endTimeStr)
                            : (startTime != null ? startTime.plusHours(1) : null); // 기본 1시간

                    schedules = parseSchedulesFromDayOfWeek(dayOfWeekString, startTime, endTime);
                }

                // 계약 관련 필드
                String contractTypeStr = getCellStringSafe(row, headerMap, "contract_type");
                ContractType contractType = ContractType.fromLabel(contractTypeStr);
                Integer weekCount = parseInteger(getCellStringSafe(row, headerMap, "week_count"));
                Integer unitPrice = parseInteger(getCellStringSafe(row, headerMap, "unit_price"));
                String level = getCellStringSafe(row, headerMap, "level");
                String place = getCellStringSafe(row, headerMap, "place");
                String emergencyContact = getCellStringSafe(row, headerMap, "emergency_contact");

                // weekCount 자동 보정
                if (weekCount == null && schedules != null) {
                    weekCount = schedules.size();
                }

                ContractExcelSaveDto dto = new ContractExcelSaveDto(
                        name, phone, schedules, lessonSubCategory, firstLessonDate,
                        ReservationStatus.REQUESTED, contractType, weekCount, unitPrice,
                        level, place, emergencyContact, memo
                );
                rows.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file", e);
        }
        return rows;
    }

    /**
     * 스케줄 문자열 파싱: "월(14:00-15:00),수(18:00-19:00),금(20:00-21:00)"
     */
    private List<ContractScheduleDto> parseSchedulesString(String schedulesStr) {
        List<ContractScheduleDto> schedules = new ArrayList<>();
        String[] scheduleArray = schedulesStr.split(",");

        for (String schedule : scheduleArray) {
            schedule = schedule.trim();
            // "월(14:00-15:00)" 형식 파싱
            int openParen = schedule.indexOf('(');
            int closeParen = schedule.indexOf(')');

            if (openParen == -1 || closeParen == -1) {
                throw new IllegalArgumentException("잘못된 스케줄 형식: " + schedule + ". 올바른 형식: 월(14:00-15:00)");
            }

            String dayOfWeek = schedule.substring(0, openParen).trim();
            DayOfWeek.valueOf(dayOfWeek);
            String timeRange = schedule.substring(openParen + 1, closeParen).trim();
            String[] times = timeRange.split("-");

            if (times.length != 2) {
                throw new IllegalArgumentException("잘못된 시간 형식: " + timeRange + ". 올바른 형식: 14:00-15:00");
            }

            LocalTime startTime = LocalTime.parse(times[0].trim());
            LocalTime endTime = LocalTime.parse(times[1].trim());

            schedules.add(new ContractScheduleDto(KoreanDayOfWeekUtil.getDayOfWeekNum(dayOfWeek), dayOfWeek, startTime, endTime));
        }

        return schedules;
    }

    /**
     * 기존 방식 파싱: day_of_week="월,수,금" + start_time="14:00" (모든 요일 같은 시간)
     */
    private List<ContractScheduleDto> parseSchedulesFromDayOfWeek(String dayOfWeekString, LocalTime startTime, LocalTime endTime) {
        if (dayOfWeekString == null) throw new IllegalArgumentException("요일 값이 null입니다.");

        List<ContractScheduleDto> schedules = new ArrayList<>();
        String[] days = dayOfWeekString.split(",");

        for (String day : days) {
            String dayTrimmed = day.trim();
            if (!dayTrimmed.isEmpty()) {
                schedules.add(new ContractScheduleDto(KoreanDayOfWeekUtil.getDayOfWeekNum(dayTrimmed), dayTrimmed, startTime, endTime));
            }
        }

        return schedules;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null && !ExcelParser.getCellString(row.getCell(i)).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            // 쉼표 제거 후 파싱
            return Integer.parseInt(value.replaceAll("[,\\s]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * headerMap과 row를 이용해 안전하게 셀 값을 가져온다. 컬럼이 없으면 null 반환
     */
    private String getCellStringSafe(Row row, Map<String, Integer> headerMap, String column) {
        Integer idx = headerMap.get(column);
        if (idx == null || idx < 0) return null;
        return ExcelParser.getCellString(row.getCell(idx));
    }
}
