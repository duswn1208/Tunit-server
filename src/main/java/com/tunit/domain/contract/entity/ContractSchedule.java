package com.tunit.domain.contract.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "contract_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ContractSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_no")
    @JsonIgnore
    @Setter
    private StudentTutorContract contract;

    private Integer dayOfWeekNum;
    private LocalTime startTime;
    private LocalTime endTime;

    public static ContractSchedule of(StudentTutorContract contract, Integer dayOfWeekNum, LocalTime startTime, LocalTime endTime) {
        return ContractSchedule.builder()
                .contract(contract)
                .dayOfWeekNum(dayOfWeekNum)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
