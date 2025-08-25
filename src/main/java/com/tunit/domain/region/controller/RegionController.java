package com.tunit.domain.region.controller;

import com.tunit.domain.region.dto.RegionDto;
import com.tunit.domain.region.dto.SubRegionDto;
import com.tunit.domain.region.service.RegionInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionInitService regionInitService;

    @GetMapping
    public List<RegionDto> sidos() { return regionInitService.sidos(); }

    @GetMapping("/{sidoCode}/subregions")
    public List<SubRegionDto> subregions(@PathVariable String sidoCode) {
        return regionInitService.sigungu(sidoCode);
    }

    @GetMapping("/_meta")
    public Map<String,String> meta() { return Map.of("version", regionInitService.version()); }
}
