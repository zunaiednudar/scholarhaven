package com.example.scholarhaven.controller;

import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> getCategories() {
        System.out.println("Public categories endpoint called");
        return categoryService.getAllCategories();
    }
}