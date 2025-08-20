package com.tunit.domain.tutor.entity;

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

    private String region;
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "of")
    private TutorRegion(Integer tutorRegionNo, TutorProfile tutorProfile, String region, LocalDateTime createdAt) {
        this.tutorRegionNo = tutorRegionNo;
        this.tutorProfile = tutorProfile;
        this.region = region;
        this.createdAt = createdAt;
    }

    public static TutorRegion saveFrom(TutorProfile tutorProfile, String region) {
        return TutorRegion.of()
                .tutorProfile(tutorProfile)
                .region(region)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
