package com.tunit.domain.contract.controller;

import com.tunit.domain.contract.dto.ContractPayRequestDto;
import com.tunit.domain.contract.service.ContractPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/contracts/pay")
@RequiredArgsConstructor
@Controller
@Slf4j
public class ContractPayController {

    private final ContractPayService contractPayService;

    @PostMapping("/{contractNo}/status")
    public ResponseEntity updatePaymentStatus(@PathVariable Long contractNo, @RequestBody ContractPayRequestDto contractPayRequestDto) {
        contractPayService.updatePaymentStatus(contractNo, contractPayRequestDto.getPaymentStatus());
        return ResponseEntity.ok().build();
    }

}
