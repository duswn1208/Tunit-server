package com.tunit.domain.tutor.entity;

import com.tunit.domain.region.dto.RegionSaveDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class TutorRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tutorRegionNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_profile_no", nullable = false)
    private TutorProfile tutorProfile;

    private Integer code;
    private String label;
    private String type;
    private Integer parentCode;
    private String parentLabel;

    private LocalDateTime createdAt;

    @Builder(builderMethodName = "of")
    private TutorRegion(Integer tutorRegionNo, TutorProfile tutorProfile, Integer code, String label, String type, Integer parentCode, String parentLabel, LocalDateTime createdAt) {
        this.tutorRegionNo = tutorRegionNo;
        this.tutorProfile = tutorProfile;
        this.code = code;
        this.label = label;
        this.type = type;
        this.parentCode = parentCode;
        this.parentLabel = parentLabel;
        this.createdAt = createdAt;
    }

    public static TutorRegion saveFrom(TutorProfile tutorProfile, RegionSaveDto regionSaveDto) {
        return TutorRegion.of()
                .tutorProfile(tutorProfile)
                .code(regionSaveDto.code())
                .label(regionSaveDto.label())
                .type(regionSaveDto.type())
                .parentCode(regionSaveDto.parentCode())
                .parentLabel(regionSaveDto.parentLabel())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
