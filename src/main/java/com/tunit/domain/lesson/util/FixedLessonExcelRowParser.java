package com.tunit.domain.lesson.util;

import com.tunit.common.util.ExcelParser;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.FixedLessonSaveDto;
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
public class FixedLessonExcelRowParser {
    public List<FixedLessonSaveDto> parse(MultipartFile file) {
        List<FixedLessonSaveDto> rows = new ArrayList<>();
        Map<String, Integer> headerMap = new HashMap<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return rows;
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = ExcelParser.getCellString(headerRow.getCell(i)).trim().toLowerCase();
                headerMap.put(header, i);
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isRowEmpty(row)) continue;

                String name = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("name", -1)));
                String phone = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("phone", -1)));
                LocalTime startTime = LocalTime.parse(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("start_time", -1))));
                String dayOfWeekString = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("day_of_week", -1)));
                Set<Integer> dayOfWeekIntSet = parseDayOfWeekIntSet(dayOfWeekString);
                String lesson = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("lesson", -1)));
                LocalDate firstLessonDate = LocalDate.parse(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("first_lesson_date", -1))));
                String memo = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("memo", -1)));

                FixedLessonSaveDto dto = new FixedLessonSaveDto(name, phone, startTime, dayOfWeekIntSet, lesson, firstLessonDate, ReservationStatus.REQUESTED, memo);
                rows.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file", e);
            // 파싱 실패 시 빈 리스트 반환
        }
        return rows;
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

    private Set<Integer> parseDayOfWeekIntSet(String dayOfWeekString) {
        if (dayOfWeekString == null) throw new IllegalArgumentException("요일 값이 null입니다.");
        Set<Integer> result = new HashSet<>();
        String[] days = dayOfWeekString.split(",");
        for (String day : days) {
            switch (day.trim()) {
                case "월": result.add(1); break;
                case "화": result.add(2); break;
                case "수": result.add(3); break;
                case "목": result.add(4); break;
                case "금": result.add(5); break;
                case "토": result.add(6); break;
                case "일": result.add(7); break;
                default: throw new IllegalArgumentException("잘못된 요일: " + day);
            }
        }
        return result;
    }
}
