package com.tunit.domain.region.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class RegionInitServiceTest {

    @Autowired
    private RegionInitService regionInitService;

    @Test
    public void initRegionData() throws IOException {
        regionInitService.load();
    }

}