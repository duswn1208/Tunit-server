package com.tunit.domain.lesson.util;

import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.dto.ContractExcelSaveDto;
import com.tunit.domain.contract.util.ContractExcelRowParser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ContractExcelRowParser 테스트")
class ContractExcelRowParserTest {

    private final ContractExcelRowParser parser = new ContractExcelRowParser();

    @Test
    @DisplayName("정규 레슨 엑셀 파싱 성공")
    void parseWithContract_RegularLesson_Success() throws IOException {
        // given
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("name");
        headerRow.createCell(1).setCellValue("phone");
        headerRow.createCell(2).setCellValue("start_time");
        headerRow.createCell(3).setCellValue("day_of_week");
        headerRow.createCell(4).setCellValue("lesson");
        headerRow.createCell(5).setCellValue("first_lesson_date");
        headerRow.createCell(6).setCellValue("memo");
        headerRow.createCell(7).setCellValue("contract_type");
        headerRow.createCell(8).setCellValue("total_price");
        headerRow.createCell(9).setCellValue("lesson_count");
        headerRow.createCell(10).setCellValue("level");
        headerRow.createCell(11).setCellValue("place");
        headerRow.createCell(12).setCellValue("emergency_contact");

        // 데이터 행 생성
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("홍길동");
        dataRow.createCell(1).setCellValue("010-1234-5678");
        dataRow.createCell(2).setCellValue("14:00");
        dataRow.createCell(3).setCellValue("월,수");
        dataRow.createCell(4).setCellValue("영어");
        dataRow.createCell(5).setCellValue("2025-01-06");
        dataRow.createCell(6).setCellValue("테스트 메모");
        dataRow.createCell(7).setCellValue("정규");
        dataRow.createCell(8).setCellValue("500000");
        dataRow.createCell(9).setCellValue("8");
        dataRow.createCell(10).setCellValue("초급");
        dataRow.createCell(11).setCellValue("강남역");
        dataRow.createCell(12).setCellValue("010-9999-9999");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // when
        List<ContractExcelSaveDto> result = parser.parseWithContract(file);

        // then
        assertThat(result).hasSize(1);
        ContractExcelSaveDto dto = result.get(0);
        assertThat(dto.studentName()).isEqualTo("홍길동");
        assertThat(dto.phone()).isEqualTo("010-1234-5678");
        assertThat(dto.getFirstStartTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(dto.getDayOfWeekSet()).containsExactlyInAnyOrder(1, 3); // 월, 수
        assertThat(dto.lesson().getLabel()).isEqualTo("영어");
        assertThat(dto.firstLessonDate()).isEqualTo(LocalDate.of(2025, 1, 6));
        assertThat(dto.memo()).isEqualTo("테스트 메모");
        assertThat(dto.contractType()).isEqualTo(ContractType.REGULAR);
        assertThat(dto.unitPrice()).isEqualTo(500000);
        assertThat(dto.weekCount()).isEqualTo(8);
        assertThat(dto.level()).isEqualTo("초급");
        assertThat(dto.place()).isEqualTo("강남역");
        assertThat(dto.emergencyContact()).isEqualTo("010-9999-9999");
        assertThat(dto.getWeekCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("선착순 레슨 엑셀 파싱 성공")
    void parseWithContract_FirstComeLesson_Success() throws IOException {
        // given
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("name");
        headerRow.createCell(1).setCellValue("phone");
        headerRow.createCell(2).setCellValue("start_time");
        headerRow.createCell(3).setCellValue("day_of_week");
        headerRow.createCell(4).setCellValue("lesson");
        headerRow.createCell(5).setCellValue("first_lesson_date");
        headerRow.createCell(6).setCellValue("memo");
        headerRow.createCell(7).setCellValue("contract_type");
        headerRow.createCell(8).setCellValue("total_price");
        headerRow.createCell(9).setCellValue("lesson_count");
        headerRow.createCell(10).setCellValue("level");
        headerRow.createCell(11).setCellValue("place");
        headerRow.createCell(12).setCellValue("emergency_contact");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("김철수");
        dataRow.createCell(1).setCellValue("010-2222-3333");
        dataRow.createCell(2).setCellValue("15:00");
        dataRow.createCell(3).setCellValue("화");
        dataRow.createCell(4).setCellValue("영어");
        dataRow.createCell(5).setCellValue("2025-01-07");
        dataRow.createCell(6).setCellValue("");
        dataRow.createCell(7).setCellValue("선착순");
        dataRow.createCell(8).setCellValue("100000");
        dataRow.createCell(9).setCellValue("1");
        dataRow.createCell(10).setCellValue("");
        dataRow.createCell(11).setCellValue("");
        dataRow.createCell(12).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // when
        List<ContractExcelSaveDto> result = parser.parseWithContract(file);

        // then
        assertThat(result).hasSize(1);
        ContractExcelSaveDto dto = result.get(0);
        assertThat(dto.studentName()).isEqualTo("김철수");
        assertThat(dto.contractType()).isEqualTo(ContractType.FIRSTCOME);
        assertThat(dto.unitPrice()).isEqualTo(100000);
        assertThat(dto.weekCount()).isEqualTo(1);
        assertThat(dto.getDayOfWeekSet()).containsExactly(2); // 화
        assertThat(dto.getWeekCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("체험 레슨 엑셀 파싱 성공")
    void parseWithContract_TrialLesson_Success() throws IOException {
        // given
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("name");
        headerRow.createCell(1).setCellValue("phone");
        headerRow.createCell(2).setCellValue("start_time");
        headerRow.createCell(3).setCellValue("day_of_week");
        headerRow.createCell(4).setCellValue("lesson");
        headerRow.createCell(5).setCellValue("first_lesson_date");
        headerRow.createCell(6).setCellValue("memo");
        headerRow.createCell(7).setCellValue("contract_type");
        headerRow.createCell(8).setCellValue("total_price");
        headerRow.createCell(9).setCellValue("lesson_count");
        headerRow.createCell(10).setCellValue("level");
        headerRow.createCell(11).setCellValue("place");
        headerRow.createCell(12).setCellValue("emergency_contact");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("이영희");
        dataRow.createCell(1).setCellValue("010-3333-4444");
        dataRow.createCell(2).setCellValue("16:00");
        dataRow.createCell(3).setCellValue("금");
        dataRow.createCell(4).setCellValue("영어");
        dataRow.createCell(5).setCellValue("2025-01-10");
        dataRow.createCell(6).setCellValue("");
        dataRow.createCell(7).setCellValue("체험");
        dataRow.createCell(8).setCellValue("50000");
        dataRow.createCell(9).setCellValue("1");
        dataRow.createCell(10).setCellValue("");
        dataRow.createCell(11).setCellValue("");
        dataRow.createCell(12).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // when
        List<ContractExcelSaveDto> result = parser.parseWithContract(file);

        // then
        assertThat(result).hasSize(1);
        ContractExcelSaveDto dto = result.get(0);
        assertThat(dto.studentName()).isEqualTo("이영희");
        assertThat(dto.contractType()).isEqualTo(ContractType.TRIAL);
        assertThat(dto.unitPrice()).isEqualTo(50000);
        assertThat(dto.weekCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("가격에 쉼표가 포함된 경우 정상 파싱")
    void parseWithContract_PriceWithComma_Success() throws IOException {
        // given
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < 13; i++) {
            headerRow.createCell(i);
        }
        headerRow.getCell(0).setCellValue("name");
        headerRow.getCell(1).setCellValue("phone");
        headerRow.getCell(2).setCellValue("start_time");
        headerRow.getCell(3).setCellValue("day_of_week");
        headerRow.getCell(4).setCellValue("lesson");
        headerRow.getCell(5).setCellValue("first_lesson_date");
        headerRow.getCell(6).setCellValue("memo");
        headerRow.getCell(7).setCellValue("contract_type");
        headerRow.getCell(8).setCellValue("total_price");
        headerRow.getCell(9).setCellValue("lesson_count");
        headerRow.getCell(10).setCellValue("level");
        headerRow.getCell(11).setCellValue("place");
        headerRow.getCell(12).setCellValue("emergency_contact");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("홍길동");
        dataRow.createCell(1).setCellValue("010-1234-5678");
        dataRow.createCell(2).setCellValue("14:00");
        dataRow.createCell(3).setCellValue("월");
        dataRow.createCell(4).setCellValue("영어");
        dataRow.createCell(5).setCellValue("2025-01-06");
        dataRow.createCell(6).setCellValue("");
        dataRow.createCell(7).setCellValue("정규");
        dataRow.createCell(8).setCellValue("1,000,000"); // 쉼표 포함
        dataRow.createCell(9).setCellValue("8");
        dataRow.createCell(10).setCellValue("");
        dataRow.createCell(11).setCellValue("");
        dataRow.createCell(12).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // when
        List<ContractExcelSaveDto> result = parser.parseWithContract(file);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).unitPrice()).isEqualTo(1000000);
    }

    @Test
    @DisplayName("여러 요일 파싱 성공")
    void parseWithContract_MultipleDays_Success() throws IOException {
        // given
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < 13; i++) {
            headerRow.createCell(i);
        }
        headerRow.getCell(0).setCellValue("name");
        headerRow.getCell(1).setCellValue("phone");
        headerRow.getCell(2).setCellValue("start_time");
        headerRow.getCell(3).setCellValue("day_of_week");
        headerRow.getCell(4).setCellValue("lesson");
        headerRow.getCell(5).setCellValue("first_lesson_date");
        headerRow.getCell(6).setCellValue("memo");
        headerRow.getCell(7).setCellValue("contract_type");
        headerRow.getCell(8).setCellValue("total_price");
        headerRow.getCell(9).setCellValue("lesson_count");
        headerRow.getCell(10).setCellValue("level");
        headerRow.getCell(11).setCellValue("place");
        headerRow.getCell(12).setCellValue("emergency_contact");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("홍길동");
        dataRow.createCell(1).setCellValue("010-1234-5678");
        dataRow.createCell(2).setCellValue("14:00");
        dataRow.createCell(3).setCellValue("월,화,수,목,금"); // 5일
        dataRow.createCell(4).setCellValue("영어");
        dataRow.createCell(5).setCellValue("2025-01-06");
        dataRow.createCell(6).setCellValue("");
        dataRow.createCell(7).setCellValue("정규");
        dataRow.createCell(8).setCellValue("500000");
        dataRow.createCell(9).setCellValue("20");
        dataRow.createCell(10).setCellValue("");
        dataRow.createCell(11).setCellValue("");
        dataRow.createCell(12).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // when
        List<ContractExcelSaveDto> result = parser.parseWithContract(file);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeekSet()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
        assertThat(result.get(0).getWeekCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("빈 행은 건너뛰기")
    void parseWithContract_SkipEmptyRows() throws IOException {
        // given
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < 13; i++) {
            headerRow.createCell(i);
        }
        headerRow.getCell(0).setCellValue("name");
        headerRow.getCell(1).setCellValue("phone");
        headerRow.getCell(2).setCellValue("start_time");
        headerRow.getCell(3).setCellValue("day_of_week");
        headerRow.getCell(4).setCellValue("lesson");
        headerRow.getCell(5).setCellValue("first_lesson_date");
        headerRow.getCell(6).setCellValue("memo");
        headerRow.getCell(7).setCellValue("contract_type");
        headerRow.getCell(8).setCellValue("total_price");
        headerRow.getCell(9).setCellValue("lesson_count");
        headerRow.getCell(10).setCellValue("level");
        headerRow.getCell(11).setCellValue("place");
        headerRow.getCell(12).setCellValue("emergency_contact");

        // 빈 행
        sheet.createRow(1);

        // 데이터 행
        Row dataRow = sheet.createRow(2);
        dataRow.createCell(0).setCellValue("홍길동");
        dataRow.createCell(1).setCellValue("010-1234-5678");
        dataRow.createCell(2).setCellValue("14:00");
        dataRow.createCell(3).setCellValue("월");
        dataRow.createCell(4).setCellValue("영어");
        dataRow.createCell(5).setCellValue("2025-01-06");
        dataRow.createCell(6).setCellValue("");
        dataRow.createCell(7).setCellValue("정규");
        dataRow.createCell(8).setCellValue("500000");
        dataRow.createCell(9).setCellValue("8");
        dataRow.createCell(10).setCellValue("");
        dataRow.createCell(11).setCellValue("");
        dataRow.createCell(12).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // when
        List<ContractExcelSaveDto> result = parser.parseWithContract(file);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("계약 타입 REGULAR 변환 테스트")
    void parseWithContract_ContractTypeRegularVariations() throws IOException {
        String[] regularTypes = {"정규", "정규레슨", "REGULAR"};

        for (String type : regularTypes) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("name");
            headerRow.createCell(1).setCellValue("phone");
            headerRow.createCell(2).setCellValue("start_time");
            headerRow.createCell(3).setCellValue("day_of_week");
            headerRow.createCell(4).setCellValue("lesson");
            headerRow.createCell(5).setCellValue("first_lesson_date");
            headerRow.createCell(6).setCellValue("memo");
            headerRow.createCell(7).setCellValue("contract_type");
            headerRow.createCell(8).setCellValue("total_price");
            headerRow.createCell(9).setCellValue("lesson_count");
            headerRow.createCell(10).setCellValue("level");
            headerRow.createCell(11).setCellValue("place");
            headerRow.createCell(12).setCellValue("emergency_contact");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("홍길동");
            dataRow.createCell(1).setCellValue("010-1234-5678");
            dataRow.createCell(2).setCellValue("14:00");
            dataRow.createCell(3).setCellValue("월");
            dataRow.createCell(4).setCellValue("영어");
            dataRow.createCell(5).setCellValue("2025-01-06");
            dataRow.createCell(6).setCellValue("");
            dataRow.createCell(7).setCellValue(type);
            dataRow.createCell(8).setCellValue("500000");
            dataRow.createCell(9).setCellValue("8");
            dataRow.createCell(10).setCellValue("");
            dataRow.createCell(11).setCellValue("");
            dataRow.createCell(12).setCellValue("");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();

            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    bos.toByteArray()
            );

            List<ContractExcelSaveDto> result = parser.parseWithContract(file);
            assertThat(result.get(0).contractType()).isEqualTo(ContractType.REGULAR);
        }
    }
}
