package com.example.scholarhaven.service;

import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
    }

    public List<Category> getCategoriesWithBooks() {
        return categoryRepository.findCategoriesWithBooks();
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getBookCountsByCategory() {
        return categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        c -> categoryRepository.countBooksById(c.getId())
                ));
    }
}