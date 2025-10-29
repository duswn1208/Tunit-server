package com.tunit.domain.contract.entity;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_tutor_contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentTutorContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractNo;

    private Long tutorProfileNo;
    private Long studentNo;
    private LocalDate startDt;
    private LocalDate endDt;

    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    private LessonSubCategory lessonSubCategory;

    private Integer lessonCount;
    private Integer weekCount;
    private String lessonName;

    private String region;
    private String memo;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

