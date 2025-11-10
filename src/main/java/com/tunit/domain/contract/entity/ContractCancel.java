package com.tunit.domain.contract.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 계약 취소/환불 정보
 * 계약이 취소된 경우에만 생성되는 엔티티
 */
@Entity
@Table(name = "contract_cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractCancel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ContractCancelNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_no", nullable = false, unique = true)
    private StudentTutorContract contract;

    private LocalDateTime canceledAt; // 취소 일시
    private String cancelReason; // 취소 사유
    private Long canceledBy; // 취소 요청자 (userNo)

    private Integer refundAmount; // 환불 금액
    private LocalDateTime refundRequestedAt; // 환불 요청 일시
    private LocalDateTime refundCompletedAt; // 환불 완료 일시

    private String refundMethod; // 환불 방법 (계좌이체, 카드취소 등)
    private String refundAccount; // 환불 계좌 정보

    private String adminMemo; // 관리자 메모

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public ContractCancel(StudentTutorContract contract, LocalDateTime canceledAt,
                          String cancelReason, Long canceledBy, Integer refundAmount,
                          LocalDateTime refundRequestedAt, LocalDateTime refundCompletedAt,
                          String refundMethod, String refundAccount, String adminMemo) {
        this.contract = contract;
        this.canceledAt = canceledAt;
        this.cancelReason = cancelReason;
        this.canceledBy = canceledBy;
        this.refundAmount = refundAmount;
        this.refundRequestedAt = refundRequestedAt;
        this.refundCompletedAt = refundCompletedAt;
        this.refundMethod = refundMethod;
        this.refundAccount = refundAccount;
        this.adminMemo = adminMemo;
    }

    /**
     * 계약 취소 정보 생성
     */
    public static ContractCancel createFrom(StudentTutorContract contract, String reason, Long canceledBy) {
        return ContractCancel.builder()
                .contract(contract)
                .canceledAt(LocalDateTime.now())
                .cancelReason(reason)
                .canceledBy(canceledBy)
                .build();
    }

    /**
     * 환불 정보 업데이트
     */
    public void updateRefundInfo(Integer refundAmount, String refundMethod, String refundAccount) {
        this.refundAmount = refundAmount;
        this.refundRequestedAt = LocalDateTime.now();
        this.refundMethod = refundMethod;
        this.refundAccount = refundAccount;
    }

    /**
     * 환불 완료 처리
     */
    public void completeRefund() {
        this.refundCompletedAt = LocalDateTime.now();
    }

    /**
     * 관리자 메모 추가
     */
    public void updateAdminMemo(String memo) {
        this.adminMemo = memo;
    }
}

