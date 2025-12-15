package com.tunit.domain.contract.service;

import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.repository.ContractScheduleRepository;
import com.tunit.domain.contract.repository.StudentTutorContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractQueryService {
    private final StudentTutorContractRepository contractRepository;

    public StudentTutorContract createContract(ContractCreateRequestDto requestDto, Integer durationMin) {
        return contractRepository.save(StudentTutorContract.createContractOf(requestDto, durationMin));
    }

    @Transactional
    public StudentTutorContract modifyContract(Long studentNo, Long contractNo, ContractCreateRequestDto requestDto) {
        StudentTutorContract contract = getContractByStudentNoAndContractNo(studentNo, contractNo);
        contract.modifyContract(studentNo, requestDto);
        return contract;
    }

    private StudentTutorContract getContractByStudentNoAndContractNo(Long studentNo, Long contractNo) {
        return contractRepository.findByContractNoAndStudentNo(contractNo, studentNo)
                .orElseThrow(() -> new ContractException("계약을 찾을 수 없습니다."));
    }

    public StudentTutorContract getContract(Long contractNo) {
        return contractRepository.findById(contractNo)
                .orElseThrow(() -> new ContractException("계약을 찾을 수 없습니다."));
    }

    public List<ContractResponseDto> getStudentContracts(Long studentNo) {
        return contractRepository.findByStudentNo(studentNo).stream()
                .map(ContractResponseDto::fromEntity)
                .toList();
    }

    public List<ContractResponseDto> getTutorContracts(Long tutorProfileNo) {
        return contractRepository.findByTutorProfileNo(tutorProfileNo).stream()
                .map(ContractResponseDto::fromEntity)
                .toList();
    }

}
