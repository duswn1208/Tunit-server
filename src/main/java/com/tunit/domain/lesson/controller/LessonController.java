package com.tunit.domain.lesson.controller;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    //todo: cache 적용 필요
    @GetMapping("/categories")
    public List<LessonCategory> getLessonCategories() {
        return List.of(LessonCategory.values());
    }

    @GetMapping("/categories/{code}/subcategories")
    public List<LessonSubCategory> getLessonSubcategories(@PathVariable("code") String code) {
        return LessonCategory.fromCode(code).getSubCategories();
    }

}
