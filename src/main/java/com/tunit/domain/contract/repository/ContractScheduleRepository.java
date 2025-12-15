package com.tunit.domain.contract.repository;

import com.tunit.domain.contract.entity.ContractSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractScheduleRepository extends JpaRepository<ContractSchedule, Long> {

    List<ContractSchedule> findByContract_ContractNo(Long contractNo);

    void deleteByContract_ContractNo(Long contractNo);
}
