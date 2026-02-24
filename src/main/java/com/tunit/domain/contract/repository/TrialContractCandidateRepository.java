package com.tunit.domain.contract.repository;

import com.tunit.domain.contract.entity.TrialContractCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrialContractCandidateRepository extends JpaRepository<TrialContractCandidate, Long> {
    
    List<TrialContractCandidate> findByContract_ContractNo(Long contractNo);
    
    List<TrialContractCandidate> findByContract_ContractNoOrderByPriority(Long contractNo);
    
    Optional<TrialContractCandidate> findByContract_ContractNoAndCandidateDateAndCandidateStartTime(
        Long contractNo,
        LocalDate date,
        LocalTime time
    );
    
    long countByContract_ContractNo(Long contractNo);
}
