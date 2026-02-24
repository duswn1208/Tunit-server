package com.tunit.domain.contract.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "trial_contract_proposals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TrialContractProposal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_no", nullable = false)
    private StudentTutorContract contract;
    
    @Column(name = "proposed_date", nullable = false)
    private LocalDate proposedDate;
    
    @Column(name = "proposed_start_time", nullable = false)
    private LocalTime proposedStartTime;
    
    @Setter
    @Column(name = "is_accepted")
    private Boolean isAccepted;  // null: 대기, true: 수락, false: 거절
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    public static TrialContractProposal of(
        StudentTutorContract contract,
        LocalDate date,
        LocalTime time
    ) {
        return TrialContractProposal.builder()
            .contract(contract)
            .proposedDate(date)
            .proposedStartTime(time)
            .build();
    }
    
    public boolean isPending() {
        return isAccepted == null;
    }
    
    public boolean isAccepted() {
        return Boolean.TRUE.equals(isAccepted);
    }
}
