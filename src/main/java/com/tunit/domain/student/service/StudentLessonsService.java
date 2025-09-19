package com.tunit.domain.student.service;

import com.tunit.domain.student.entity.StudentLessons;
import com.tunit.domain.student.repository.StudentLessonsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentLessonsService {
    private final StudentLessonsRepository studentLessonRepository;

    public StudentLessons save(StudentLessons studentLesson) {
        return studentLessonRepository.save(studentLesson);
    }

    public Set<StudentLessons> findByUserNo(Long userNo) {
        return studentLessonRepository.findAllByUserNo(userNo);
    }

    public void delete(Long studentLessonNo) {
        studentLessonRepository.deleteById(studentLessonNo);
    }

    public void saveAll(List<StudentLessons> list) {
        studentLessonRepository.saveAll(list);
    }
}

