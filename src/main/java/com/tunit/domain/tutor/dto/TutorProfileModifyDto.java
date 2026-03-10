package com.tunit.domain.tutor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TutorProfileModifyDto {
    private Long userNo;
    private String nickname;
    private String introduce;
    private Integer careerYears;
    private Integer pricePerHour;
    private Integer durationMin;
    private String bankName;
    private String accountNumber;
    private String accountHolder;

    @Builder(builderMethodName = "of")
    public TutorProfileModifyDto(Long userNo, String nickname, String introduce, Integer careerYears, Integer pricePerHour, Integer durationMin,
                                 String bankName, String accountNumber, String accountHolder) {
        this.userNo = userNo;
        this.nickname = nickname;
        this.introduce = introduce;
        this.careerYears = careerYears;
        this.pricePerHour = pricePerHour;
        this.durationMin = durationMin;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }

}
