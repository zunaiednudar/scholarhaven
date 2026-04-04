package com.example.scholarhaven.service;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.BookRepository;
import com.example.scholarhaven.strategy.book.BookStrategyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookStrategyContext strategyContext;

    @InjectMocks
    private BookServiceImpl bookService;

    private User testSeller;
    private Category testCategory;
    private Book testBook;
    private BookRequestDTO testBookRequest;

    @BeforeEach
    void setUp() {
        testSeller = new User();
        testSeller.setId(1L);
        testSeller.setUsername("seller");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fiction");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPrice(new BigDecimal("29.99"));
        testBook.setStock(10);
        testBook.setStatus(Book.BookStatus.AVAILABLE);
        testBook.setSeller(testSeller);
        testBook.setCategory(testCategory);

        testBookRequest = new BookRequestDTO();
        testBookRequest.setTitle("New Book");
        testBookRequest.setAuthor("New Author");
        testBookRequest.setPrice(new BigDecimal("19.99"));
        testBookRequest.setStock(5);
        testBookRequest.setCategoryId(1L);
    }

    @Test
    void testGetBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        BookResponseDTO result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals(new BigDecimal("29.99"), result.getOriginalPrice());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.getBookById(99L));
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        List<BookResponseDTO> results = bookService.getAllBooks();

        assertEquals(1, results.size());
        assertEquals("Test Book", results.get(0).getTitle());
    }

    @Test
    void testGetAllAvailableBooks() {
        when(bookRepository.findByStatus(Book.BookStatus.AVAILABLE)).thenReturn(Arrays.asList(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        List<BookResponseDTO> results = bookService.getAllAvailableBooks();

        assertEquals(1, results.size());
        assertEquals("Test Book", results.get(0).getTitle());
    }

    @Test
    void testGetBooksBySeller() {
        when(bookRepository.findBySeller(testSeller)).thenReturn(Arrays.asList(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        List<BookResponseDTO> results = bookService.getBooksBySeller(testSeller);

        assertEquals(1, results.size());
        assertEquals("Test Book", results.get(0).getTitle());
    }

    @Test
    void testGetBooksByCategory() {
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(bookRepository.findByCategoryAndStatus(testCategory, Book.BookStatus.AVAILABLE))
            .thenReturn(Arrays.asList(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        List<BookResponseDTO> results = bookService.getBooksByCategory(1L);

        assertEquals(1, results.size());
        assertEquals("Test Book", results.get(0).getTitle());
    }

    @Test
    void testSearchBooks() {
        when(bookRepository.searchBooks("test")).thenReturn(Arrays.asList(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        List<BookResponseDTO> results = bookService.searchBooks("test");

        assertEquals(1, results.size());
        assertEquals("Test Book", results.get(0).getTitle());
    }

    @Test
    void testCreateBook() {
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        doNothing().when(strategyContext).setValidationStrategyForUser(any(User.class));
        doNothing().when(strategyContext).validateBook(any(Book.class), any(User.class));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        BookResponseDTO result = bookService.createBook(testBookRequest, testSeller);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        doNothing().when(strategyContext).setValidationStrategyForUser(any(User.class));
        doNothing().when(strategyContext).validateBook(any(Book.class), any(User.class));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        BookResponseDTO result = bookService.updateBook(1L, testBookRequest, testSeller);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(any(Book.class));

        bookService.deleteBook(1L, testSeller);

        verify(bookRepository, times(1)).delete(testBook);
    }

    @Test
    void testGetFeaturedBooks() {
        when(bookRepository.findByFeaturedTrue()).thenReturn(Arrays.asList(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        List<BookResponseDTO> results = bookService.getFeaturedBooks();

        assertEquals(1, results.size());
    }

    @Test
    void testGetRecentBooks() {
        when(bookRepository.findRecentBooks(any())).thenReturn(Arrays.asList(testBook));
        when(strategyContext.calculatePrice(any())).thenReturn(new BigDecimal("29.99"));
        when(strategyContext.getCurrentPricingStrategyName()).thenReturn("Standard Price");

        List<BookResponseDTO> results = bookService.getRecentBooks(5);

        assertEquals(1, results.size());
    }
}