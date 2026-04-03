package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.CategoryService;
import com.example.scholarhaven.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookApiControllerUnitTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private BookApiController bookApiController;

    private User testUser;
    private BookResponseDTO testBook;
    private List<BookResponseDTO> testBooks;
    private BookRequestDTO testBookRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBook = new BookResponseDTO();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setOriginalPrice(new BigDecimal("29.99"));
        testBook.setFinalPrice(new BigDecimal("29.99"));

        testBooks = Arrays.asList(testBook);

        testBookRequest = new BookRequestDTO();
        testBookRequest.setTitle("New Book");
        testBookRequest.setAuthor("New Author");
        testBookRequest.setPrice(new BigDecimal("19.99"));
        testBookRequest.setStock(5);
        testBookRequest.setCategoryId(1L);
    }

    @Test
    void testGetAllBooks() {
        when(bookService.getAllAvailableBooks()).thenReturn(testBooks);

        ResponseEntity<List<BookResponseDTO>> response = bookApiController.getAllBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Book", response.getBody().get(0).getTitle());
    }

    @Test
    void testGetBookById() {
        when(bookService.getBookById(1L)).thenReturn(testBook);

        ResponseEntity<BookResponseDTO> response = bookApiController.getBookById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Book", response.getBody().getTitle());
    }

    @Test
    void testSearchBooks() {
        when(bookService.searchBooks("java")).thenReturn(testBooks);

        ResponseEntity<List<BookResponseDTO>> response = bookApiController.searchBooks("java");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetBooksByCategory() {
        when(bookService.getBooksByCategory(1L)).thenReturn(testBooks);

        ResponseEntity<List<BookResponseDTO>> response = bookApiController.getBooksByCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetFeaturedBooks() {
        when(bookService.getFeaturedBooks()).thenReturn(testBooks);

        ResponseEntity<List<BookResponseDTO>> response = bookApiController.getFeaturedBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetRecentBooks() {
        when(bookService.getRecentBooks(8)).thenReturn(testBooks);

        ResponseEntity<List<BookResponseDTO>> response = bookApiController.getRecentBooks(8);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAvailablePricingStrategies() {
        Map<String, String> strategies = Map.of("STANDARD", "Standard Price");
        when(bookService.getAvailablePricingStrategies()).thenReturn(strategies);

        ResponseEntity<Map<String, String>> response = bookApiController.getAvailablePricingStrategies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("STANDARD"));
    }

    @Test
    void testGetMyBooks() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(bookService.getBooksBySeller(testUser)).thenReturn(testBooks);

        ResponseEntity<List<BookResponseDTO>> response = bookApiController.getMyBooks(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCreateBook() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(bookService.createBook(any(BookRequestDTO.class), eq(testUser))).thenReturn(testBook);

        ResponseEntity<BookResponseDTO> response = bookApiController.createBook(testBookRequest, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateBook() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(bookService.updateBook(eq(1L), any(BookRequestDTO.class), eq(testUser))).thenReturn(testBook);

        ResponseEntity<BookResponseDTO> response = bookApiController.updateBook(1L, testBookRequest, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteBook() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        doNothing().when(bookService).deleteBook(1L, testUser);

        ResponseEntity<?> response = bookApiController.deleteBook(1L, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookService).deleteBook(1L, testUser);
    }

    @Test
    void testDeleteBook_ShouldReturnForbidden_WhenUserHasNoPermission() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        doThrow(new RuntimeException("You don't have permission to delete this book"))
            .when(bookService).deleteBook(1L, testUser);

        ResponseEntity<?> response = bookApiController.deleteBook(1L, userDetails);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(bookService).deleteBook(1L, testUser);
    }

    @Test
    void testGetPendingApprovalBooks() {
        when(bookService.getPendingApprovalBooks()).thenReturn(testBooks);

        ResponseEntity<List<BookResponseDTO>> response = bookApiController.getPendingApprovalBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}