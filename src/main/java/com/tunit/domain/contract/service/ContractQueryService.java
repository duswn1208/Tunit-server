package com.tunit.domain.contract.service;

import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.repository.StudentTutorContractRepository;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractQueryService {
    private final StudentTutorContractRepository contractRepository;

    public StudentTutorContract createContract(ContractCreateRequestDto requestDto) {
        return contractRepository.save(StudentTutorContract.createContractOf(requestDto));
    }

    public StudentTutorContract getContract(Long contractNo) {
        return contractRepository.findById(contractNo)
                .orElseThrow(() -> new ContractException("계약을 찾을 수 없습니다."));
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
