package com.tunit.domain.contract.repository;

import com.tunit.domain.contract.entity.TrialContractProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrialContractProposalRepository extends JpaRepository<TrialContractProposal, Long> {
    
    List<TrialContractProposal> findByContract_ContractNo(Long contractNo);
    
    List<TrialContractProposal> findByContract_ContractNoAndIsAcceptedIsNull(Long contractNo);
}
