package com.tunit.common.util;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExcelParser {
    public static <T> List<T> parse(MultipartFile file, Function<Row, T> rowMapper) {
        List<T> result = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 0번 row는 헤더로 가정
                Row row = sheet.getRow(i);
                if (row == null) continue;
                T obj = rowMapper.apply(row);
                if (obj != null) result.add(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파싱 오류: " + e.getMessage(), e);
        }
        return result;
    }

    public static String getCellString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalTime().toString();
            }
            return String.valueOf((long)cell.getNumericCellValue());
        }
        return "";
    }
}

