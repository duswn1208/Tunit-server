package com.tunit.domain.lesson.util;

import com.tunit.common.util.ExcelParser;
import com.tunit.domain.lesson.dto.FixedLessonExcelDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Component
public class FixedLessonExcelRowParser {
    public List<FixedLessonExcelDto> parse(MultipartFile file) {
        List<FixedLessonExcelDto> rows = new ArrayList<>();
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
                FixedLessonExcelDto dto = new FixedLessonExcelDto();
                dto.setName(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("name", -1))));
                dto.setPhone(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("phone", -1))));
                dto.setStartTime(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("start_time", -1))));
                dto.setDayOfWeek(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("day_of_week", -1))));
                dto.setLesson(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("lesson", -1))));
                dto.setFirstLessonDate(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("first_lesson_date", -1))));
                dto.setMemo(ExcelParser.getCellString(row.getCell(headerMap.getOrDefault("memo", -1))));
                rows.add(dto);
            }
        } catch (Exception e) {
            // 파싱 실패 시 빈 리스트 반환
        }
        return rows;
    }
}

