package com.tunit.domain.student.service;

import com.tunit.domain.student.entity.StudentRegions;
import com.tunit.domain.student.repository.StudentRegionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentRegionsService {
    private final StudentRegionsRepository studentRegionsRepository;

    public StudentRegions save(StudentRegions studentRegion) {
        return studentRegionsRepository.save(studentRegion);
    }

    public Set<StudentRegions> findByUserNo(Long userNo) {
        return studentRegionsRepository.findAllByUserNo(userNo);
    }

    public void delete(Long studentRegionNo) {
        studentRegionsRepository.deleteById(studentRegionNo);
    }

    public void saveAll(List<StudentRegions> list) {
        studentRegionsRepository.saveAll(list);
    }
}

