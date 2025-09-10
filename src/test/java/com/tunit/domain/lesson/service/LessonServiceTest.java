package com.tunit.domain.lesson.service;

import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.repository.TutorLessonsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonServiceTest {
    @Mock
    private TutorLessonsRepository tutorLessonsRepository;
    @InjectMocks
    private LessonService lessonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getLessonCategoriesByTutor_success() {
        // given
        Long tutorProfileNo = 1L;
        TutorLessons lesson1 = mock(TutorLessons.class);
        TutorLessons lesson2 = mock(TutorLessons.class);
        List<TutorLessons> lessons = Arrays.asList(lesson1, lesson2);
        when(tutorLessonsRepository.findByTutorProfile_TutorProfileNo(tutorProfileNo)).thenReturn(lessons);

        // when
        List<TutorLessons> result = lessonService.getLessonCategoriesByTutor(tutorProfileNo);

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(lesson1));
        assertTrue(result.contains(lesson2));
        verify(tutorLessonsRepository, times(1)).findByTutorProfile_TutorProfileNo(tutorProfileNo);
    }
}

