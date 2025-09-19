package com.tunit.domain.student.entity;

import com.tunit.domain.region.dto.RegionSaveDto;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.entity.TutorRegion;
import com.tunit.domain.user.entity.UserMain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class StudentRegions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentRegionNo;

    private Long userNo;
    private Integer code;
    private String label;
    private String type;
    private Integer parentCode;
    private String parentLabel;

    private LocalDateTime createdAt;

    @Builder(builderMethodName = "of")
    public StudentRegions(Long studentRegionNo, Long userNo, Integer code, String label, String type, Integer parentCode, String parentLabel, LocalDateTime createdAt) {
        this.studentRegionNo = studentRegionNo;
        this.userNo = userNo;
        this.code = code;
        this.label = label;
        this.type = type;
        this.parentCode = parentCode;
        this.parentLabel = parentLabel;
        this.createdAt = createdAt;
    }

    public static StudentRegions saveFrom(Long userNo, RegionSaveDto regionSaveDto) {
        return StudentRegions.of()
                .userNo(userNo)
                .code(regionSaveDto.code())
                .label(regionSaveDto.label())
                .type(regionSaveDto.type())
                .parentCode(regionSaveDto.parentCode())
                .parentLabel(regionSaveDto.parentLabel())
                .createdAt(LocalDateTime.now())
                .build();
    }

}

