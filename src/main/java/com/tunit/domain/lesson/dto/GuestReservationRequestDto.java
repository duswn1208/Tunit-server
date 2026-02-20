package com.tunit.domain.lesson.dto;

import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.dto.BaseContractDto;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 비회원 예약 요청 DTO
 * BaseContractDto를 상속받아 학생 정보(studentName, phone)만 추가
 * 체험레슨(TRIAL)으로 고정
 */
@Getter
public class GuestReservationRequestDto extends BaseContractDto {
    
    // 비회원 전용 필드 (학생 정보)
    private final String studentName;
    private final String phone;
    
    /**
     * Jackson deserialization용 기본 생성자
     */
    public GuestReservationRequestDto() {
        super(ContractType.TRIAL, null, null, null, null, null, null, null);
        this.studentName = null;
        this.phone = null;
    }
    
    /**
     * 전체 필드 생성자 - contractType은 항상 TRIAL로 고정
     */
    public GuestReservationRequestDto(String studentName, String phone,
                                     LessonSubCategory lessonCategory,
                                     LocalDate lessonDate, LocalTime startTime,
                                     Integer totalPrice, String place, String level, String memo) {
        super(ContractType.TRIAL, lessonCategory, lessonDate, startTime, 
              totalPrice, place, level, memo);
        this.studentName = studentName;
        this.phone = phone;
    }
    
    /**
     * 필수 필드만 생성자
     */
    public GuestReservationRequestDto(String studentName, String phone,
                                     LessonSubCategory lessonCategory,
                                     LocalDate lessonDate, LocalTime startTime) {
        this(studentName, phone, lessonCategory, lessonDate, startTime, 
             null, null, null, null);
    }
    
    /**
     * 객체 생성 후 TRIAL 타입 보장
     */
    public GuestReservationRequestDto ensureTrialType() {
        if (this.contractType != ContractType.TRIAL) {
            return new GuestReservationRequestDto(
                this.studentName, this.phone, this.lessonCategory,
                this.lessonDate, this.startTime,
                this.totalPrice, this.place, this.level, this.memo
            );
        }
        return this;
    }
}

