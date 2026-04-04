
package com.example.scholarhaven.service;

import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");
        category1.setDescription("Fictional books");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Non-Fiction");
        category2.setDescription("Non-fictional books");
    }

    @Test
    void testGetAllCategories_ShouldReturnAllCategories() {
        List<Category> expected = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(expected);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Fiction", result.get(0).getName());
        assertEquals("Non-Fiction", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCategories_EmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<Category> result = categoryService.getAllCategories();

        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryById_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Fiction", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryById_NotFound_ShouldThrowException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(99L);
        });

        assertTrue(exception.getMessage().contains("Category not found"));
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void testGetCategoryByName_ShouldReturnCategory() {
        when(categoryRepository.findByName("Fiction")).thenReturn(Optional.of(category1));

        Category result = categoryService.getCategoryByName("Fiction");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository, times(1)).findByName("Fiction");
    }

    @Test
    void testGetCategoryByName_NotFound_ShouldThrowException() {
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryByName("Unknown");
        });

        assertTrue(exception.getMessage().contains("Category not found with name: Unknown"));
    }

    @Test
    void testGetCategoriesWithBooks() {
        when(categoryRepository.findCategoriesWithBooks()).thenReturn(Arrays.asList(category1));

        List<Category> result = categoryService.getCategoriesWithBooks();

        assertEquals(1, result.size());
        assertEquals("Fiction", result.get(0).getName());
        verify(categoryRepository, times(1)).findCategoriesWithBooks();
    }

    @Test
    void testGetBookCountsByCategory() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
        when(categoryRepository.countBooksById(1L)).thenReturn(5L);
        when(categoryRepository.countBooksById(2L)).thenReturn(10L);

        Map<Long, Long> result = categoryService.getBookCountsByCategory();

        assertEquals(2, result.size());
        assertEquals(5L, result.get(1L));
        assertEquals(10L, result.get(2L));
        verify(categoryRepository, times(1)).countBooksById(1L);
        verify(categoryRepository, times(1)).countBooksById(2L);
    }

    @Test
    void testGetBookCountsByCategory_EmptyCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        Map<Long, Long> result = categoryService.getBookCountsByCategory();

        assertTrue(result.isEmpty());
        verify(categoryRepository, never()).countBooksById(anyLong());
    }
}
