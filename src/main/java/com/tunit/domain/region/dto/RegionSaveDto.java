package com.tunit.domain.region.dto;

public record RegionSaveDto(
        Integer code,
        String label,
        String type,
        Integer parentCode,
        String parentLabel
) {

}
