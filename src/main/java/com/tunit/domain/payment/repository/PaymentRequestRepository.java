package com.tunit.domain.payment.repository;

import com.tunit.domain.payment.entity.PaymentRequest;
import com.tunit.domain.payment.define.PaymentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

    List<PaymentRequest> findByContractNo(Long contractNo);

    List<PaymentRequest> findByStudentNo(Long studentNo);

    List<PaymentRequest> findByTutorProfileNo(Long tutorProfileNo);

    Optional<PaymentRequest> findByContractNoAndStatus(Long contractNo, PaymentRequestStatus status);

    List<PaymentRequest> findByContractNoAndStatusIn(Long contractNo, List<PaymentRequestStatus> statuses);
}
