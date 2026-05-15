package com.tunit.domain.tutor.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.tutor.dto.TutorCareerHistoryResponseDto;
import com.tunit.domain.tutor.dto.TutorCareerHistorySaveDto;
import com.tunit.domain.tutor.service.TutorCareerHistoryService;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutor/career-history")
@RequiredArgsConstructor
public class TutorCareerHistoryController {

    private final TutorCareerHistoryService tutorCareerHistoryService;
    private final TutorProfileService tutorProfileService;

    @GetMapping("/{tutorProfileNo}")
    public ResponseEntity<List<TutorCareerHistoryResponseDto>> getCareerHistory(@PathVariable Long tutorProfileNo) {
        return ResponseEntity.ok(tutorCareerHistoryService.findByTutorProfileNo(tutorProfileNo));
    }

    // 전체 교체 저장 (순서 포함)
    @PutMapping
    public ResponseEntity<?> saveAll(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                     @RequestBody List<TutorCareerHistorySaveDto> dtoList) {
        tutorCareerHistoryService.saveAll(tutorProfileNo, dtoList);
        return ResponseEntity.ok().build();
    }

    // 단건 추가
    @PostMapping
    public ResponseEntity<Long> add(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                    @RequestBody TutorCareerHistorySaveDto dto) {
        return ResponseEntity.ok(tutorCareerHistoryService.add(tutorProfileNo, dto));
    }

    // 단건 삭제
    @DeleteMapping("/{tutorCareerHistoryNo}")
    public ResponseEntity<?> delete(@PathVariable Long tutorCareerHistoryNo) {
        tutorCareerHistoryService.delete(tutorCareerHistoryNo);
        return ResponseEntity.ok().build();
    }
}
