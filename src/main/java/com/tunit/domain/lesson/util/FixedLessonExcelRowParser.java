package com.tunit.domain.lesson.util;

import com.tunit.common.util.ExcelParser;
import com.tunit.domain.lesson.define.LessonSubCategory;
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
                if (row == null) continue;

                String name = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("studentName", -1)));
                String phone = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("phone", -1)));
                LocalTime startTime = LocalTime.parse(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("start_time", -1))));
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("day_of_week", -1))));
                String lesson = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("lesson", -1)));
                LocalDate firstLessonDate = LocalDate.parse(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("first_lesson_date", -1))));
                String memo = ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("memo", -1)));

                FixedLessonSaveDto dto = new FixedLessonSaveDto(name, phone, startTime, new HashSet<>(Collections.singleton(dayOfWeek)), lesson, firstLessonDate, ReservationStatus.REQUESTED, memo);
                rows.add(dto);
            }
        } catch (Exception e) {
            // 파싱 실패 시 빈 리스트 반환
        }
        return rows;
    }
}
