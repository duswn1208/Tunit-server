package com.tunit.domain.region.service;

import com.tunit.domain.region.dto.RegionDto;
import com.tunit.domain.region.dto.SubRegionDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegionInitService {

    private final List<RegionDto> sidos = new ArrayList<>();
    private final Map<String, List<SubRegionDto>> sigunguBySido = new LinkedHashMap<>();
    private String version = "";

    // 애플리케이션 시작 시 1회 로드
    @PostConstruct
    public void load() throws IOException {
        var resource = new org.springframework.core.io.ClassPathResource("static/region/region_list.csv");
        try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(resource.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            String header = br.readLine(); // 헤더 제거
            String line;
            // 임시 맵(시도코드->시도명)
            Map<String, String> sidoMap = new LinkedHashMap<>();

            while ((line = br.readLine()) != null) {
                // CSV 안전 파싱이 필요하면 OpenCSV 사용 고려. 단순 구분자는 split로도 가능.
                String[] t = line.split(",", -1);
                String code = t[0].trim();          // 법정동코드 (10자리일 수 있음)
                String name = t[1].trim();          // 법정동명 ("서울특별시" or "서울특별시 중구" 등)

                // 공백 기준으로 토큰 수 파악
                String[] parts = name.split("\\s+");
                if (parts.length == 1) {
                    // 시/도
                    String sidoCode = code.substring(0, 2); // 시도코드 2자리 관례
                    String sidoName = parts[0];
                    if (!sidoMap.containsKey(sidoCode)) {
                        sidoMap.put(sidoCode, sidoName);
                        sidos.add(new RegionDto(sidoCode, sidoName));
                    }
                } else {
                    // 시군구 (구까지만 저장했다 했으니 이 분기에서 처리)
                    String sidoCode = code.substring(0, 2);
                    String sidoName = sidoMap.computeIfAbsent(sidoCode, k -> parts[0]); // 혹시 앞에서 못 넣었을 경우 대비
                    // 시군구 코드는 보통 앞 5자리 사용
                    String sigunguCode = code.substring(0, 5);
                    String sigunguName = parts[parts.length - 1]; // 마지막 토큰을 구명으로 잡는 전략 (ex: "중구", "장안구")

                    // 필요하면 정교하게: "수원시 장안구" 전체를 label로 쓰려면 join
                    if (parts.length >= 2) {
                        sigunguName = String.join(" ", java.util.Arrays.copyOfRange(parts, parts.length - 2, parts.length));
                        // 예: "수원시 장안구" / "중구" 상황에 맞게 규칙 조정 가능
                    }

                    var list = sigunguBySido.computeIfAbsent(sidoCode, k -> new ArrayList<>());
                    // 중복 방지
                    boolean existsSigungu = list.stream().anyMatch(s -> s.code().equals(sigunguCode));
                    if (!existsSigungu) {
                        list.add(new SubRegionDto(sigunguCode, sigunguName, sidoCode, sidoName));
                    }
                }
            }

            // 정렬
            sidos.sort(java.util.Comparator.comparing(RegionDto::label));
            sigunguBySido.values().forEach(l -> l.sort(java.util.Comparator.comparing(SubRegionDto::label)));

            // 버전 태그(선택): 파일명이나 빌드타임으로 기록
            this.version = java.time.LocalDate.now().toString();
        }
    }

    public List<RegionDto> sidos() {
        return java.util.Collections.unmodifiableList(sidos);
    }

    public List<SubRegionDto> sigungu(String sidoCode) {
        return sigunguBySido.getOrDefault(sidoCode, java.util.Collections.emptyList());
    }

    public String version() {
        return version;
    }


}
