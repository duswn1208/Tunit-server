package com.tunit.domain.contract.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "trial_contract_candidates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TrialContractCandidate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_no", nullable = false)
    private StudentTutorContract contract;
    
    @Column(nullable = false)
    private Integer priority;  // 1, 2, 3
    
    @Column(name = "candidate_date", nullable = false)
    private LocalDate candidateDate;
    
    @Column(name = "candidate_start_time", nullable = false)
    private LocalTime candidateStartTime;
    
    @Setter
    @Column(name = "is_available")
    private Boolean isAvailable;  // 튜터 스케줄 체크 결과 (null: 미확인, true: 가능, false: 불가)
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    public static TrialContractCandidate of(
        StudentTutorContract contract,
        Integer priority,
        LocalDate date,
        LocalTime time
    ) {
        return TrialContractCandidate.builder()
            .contract(contract)
            .priority(priority)
            .candidateDate(date)
            .candidateStartTime(time)
            .build();
    }
    
    public boolean isAvailable() {
        return Boolean.TRUE.equals(isAvailable);
    }
}
