package com.tunit.domain.region.dto;

import com.tunit.domain.tutor.entity.TutorRegion;

public record RegionSaveDto(
        Integer code,
        String label,
        String type,
        Integer parentCode,
        String parentLabel
) {

    public static RegionSaveDto from(TutorRegion region) {
        return new RegionSaveDto(
                region.getCode(),
                region.getLabel(),
                region.getType(),
                region.getParentCode(),
                region.getParentLabel()
        );
    }
}
