package com.tunit.domain.contract.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.contract.dto.ContractExcelUploadResultDto;
import com.tunit.domain.contract.service.ContractExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/contract/excel")
@RequiredArgsConstructor
public class ContractExcelController {
    private final ContractExcelService contractExcelService;

    /**
     * 계약 정보를 포함한 엑셀 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<ContractExcelUploadResultDto> uploadExcelWithContract(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @RequestParam("file") MultipartFile file) {
        ContractExcelUploadResultDto result = contractExcelService.uploadExcelWithContract(tutorProfileNo, file);
        return ResponseEntity.ok(result);
    }

}
