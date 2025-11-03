package com.tunit.domain.contract.service;

import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.repository.StudentTutorContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContractService {

    private final StudentTutorContractRepository contractRepository;

    public ContractResponseDto createContract(ContractCreateRequestDto requestDto) {
        StudentTutorContract save = contractRepository.save(StudentTutorContract.createContractOf(requestDto));
        return new ContractResponseDto(save);
    }

    public ContractResponseDto getContract(Long contractNo) {
        StudentTutorContract contract = contractRepository.findById(contractNo)
                .orElseThrow(() -> new IllegalArgumentException("계약을 찾을 수 없습니다."));
        return new ContractResponseDto(contract);
    }

    public List<ContractResponseDto> getStudentContracts(Long studentNo) {
        return contractRepository.findByStudentNo(studentNo).stream()
                .map(ContractResponseDto::new)
                .toList();
    }

    public List<ContractResponseDto> getTutorContracts(Long tutorProfileNo) {
        return contractRepository.findByTutorProfileNo(tutorProfileNo).stream()
                .map(ContractResponseDto::new)
                .toList();
    }
}

