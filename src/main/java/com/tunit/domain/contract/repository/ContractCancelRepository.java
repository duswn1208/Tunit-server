package com.tunit.domain.contract.repository;

import com.tunit.domain.contract.entity.ContractCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractCancelRepository extends JpaRepository<ContractCancel, Long> {

    /**
     * 계약 번호로 취소 정보 조회
     */
    Optional<ContractCancel> findByContract_ContractNo(Long contractNo);

    /**
     * 계약 번호로 취소 존재 여부 확인
     */
    boolean existsByContract_ContractNo(Long contractNo);
}

