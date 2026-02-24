package com.tunit.domain.contract.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialRejectDto {
    
    private String reason;  // 거절 사유
    
    // 대안 시간 제안 (선택사항)
    private List<AlternativeTime> alternativeTimes;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlternativeTime {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate proposedDate;
        
        @JsonFormat(pattern = "HH:mm")
        private LocalTime proposedStartTime;
    }
    
    public boolean hasAlternatives() {
        return alternativeTimes != null && !alternativeTimes.isEmpty();
    }
}
