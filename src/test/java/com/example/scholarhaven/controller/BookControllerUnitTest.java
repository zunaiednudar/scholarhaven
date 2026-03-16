package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerUnitTest {

    @Mock
    private BookService bookService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private Model model;

    @InjectMocks
    private BookController bookController;

    private BookResponseDTO testBook;
    private Category testCategory;
    private List<BookResponseDTO> testBooks;

    @BeforeEach
    void setUp() {
        testBook = new BookResponseDTO();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setOriginalPrice(new BigDecimal("29.99"));
        testBook.setFinalPrice(new BigDecimal("29.99"));
        testBook.setCategoryId(1L);
        testBook.setCategoryName("Fiction");

        testBooks = Arrays.asList(testBook);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fiction");
    }

    @Test
    void testListBooks_NoParams() {
        when(bookService.getAllAvailableBooks()).thenReturn(testBooks);
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(testCategory));

        String result = bookController.listBooks(null, null, model);

        assertEquals("books", result);
        verify(bookService).getAllAvailableBooks();
        verify(categoryService).getAllCategories();
        verify(model).addAttribute(eq("books"), any());
        verify(model).addAttribute(eq("categories"), any());
        verify(model).addAttribute(eq("title"), eq("All Books"));
    }

    @Test
    void testListBooks_WithSearch() {
        when(bookService.searchBooks("java")).thenReturn(testBooks);
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(testCategory));

        String result = bookController.listBooks("java", null, model);

        assertEquals("books", result);
        verify(bookService).searchBooks("java");
        verify(model).addAttribute(eq("searchQuery"), eq("java"));
        verify(model).addAttribute(eq("title"), eq("Search Results for \"java\""));
    }

    @Test
    void testListBooks_WithCategory() {
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(bookService.getBooksByCategory(1L)).thenReturn(testBooks);
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(testCategory));

        String result = bookController.listBooks(null, 1L, model);

        assertEquals("books", result);
        verify(bookService).getBooksByCategory(1L);
        verify(model).addAttribute(eq("title"), eq("Fiction Books"));
        verify(model).addAttribute(eq("currentCategory"), eq(1L));
    }

    @Test
    void testViewBook() {
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(testCategory));

        String result = bookController.viewBook(1L, model);

        assertEquals("book-detail", result);
        verify(bookService).getBookById(1L);
        verify(model).addAttribute(eq("book"), eq(testBook));
    }

    @Test
    void testFeaturedBooks() {
        when(bookService.getFeaturedBooks()).thenReturn(testBooks);
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(testCategory));

        String result = bookController.featuredBooks(model);

        assertEquals("books", result);
        verify(bookService).getFeaturedBooks();
        verify(model).addAttribute(eq("title"), eq("Featured Books"));
    }

    @Test
    void testNewArrivals() {
        when(bookService.getRecentBooks(20)).thenReturn(testBooks);
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(testCategory));

        String result = bookController.newArrivals(model);

        assertEquals("books", result);
        verify(bookService).getRecentBooks(20);
        verify(model).addAttribute(eq("title"), eq("New Arrivals"));
    }
}