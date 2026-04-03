package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.UserService;
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
public class AdminControllerUnitTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    private List<BookResponseDTO> pendingBooks;
    private List<BookResponseDTO> allBooks;
    private List<User> allUsers;

    @BeforeEach
    void setUp() {
        BookResponseDTO pendingBook = new BookResponseDTO();
        pendingBook.setId(1L);
        pendingBook.setTitle("Pending Book");
        pendingBook.setAuthor("Author");
        pendingBook.setOriginalPrice(new BigDecimal("29.99"));
        pendingBook.setStatus("PENDING_APPROVAL");
        pendingBook.setSellerName("seller1");
        pendingBooks = Arrays.asList(pendingBook);

        BookResponseDTO availableBook = new BookResponseDTO();
        availableBook.setId(2L);
        availableBook.setTitle("Available Book");
        availableBook.setAuthor("Author2");
        availableBook.setOriginalPrice(new BigDecimal("39.99"));
        availableBook.setStatus("AVAILABLE");
        allBooks = Arrays.asList(pendingBook, availableBook);

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@test.com");
        user1.setEnabled(true);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");
        user2.setEnabled(false);

        allUsers = Arrays.asList(user1, user2);
    }

    @Test
    void testAdminDashboard() {
        when(bookService.getPendingApprovalBooks()).thenReturn(pendingBooks);
        when(bookService.getAllBooks()).thenReturn(allBooks);
        when(userService.findAllUsers()).thenReturn(allUsers);

        String viewName = adminController.adminDashboard(model);

        assertEquals("admin/dashboard", viewName);
        verify(bookService).getPendingApprovalBooks();
        verify(bookService).getAllBooks();
        verify(userService).findAllUsers();
        verify(model).addAttribute("pendingBooks", pendingBooks);
        verify(model).addAttribute("allBooks", allBooks);
        verify(model).addAttribute("users", allUsers);
    }

    @Test
    void testAdminDashboard_NoPendingBooks() {
        when(bookService.getPendingApprovalBooks()).thenReturn(Collections.emptyList());
        when(bookService.getAllBooks()).thenReturn(allBooks);
        when(userService.findAllUsers()).thenReturn(allUsers);

        String viewName = adminController.adminDashboard(model);

        assertEquals("admin/dashboard", viewName);
        verify(model).addAttribute("pendingBooks", Collections.emptyList());
    }

    @Test
    void testAdminDashboard_NoUsers() {
        when(bookService.getPendingApprovalBooks()).thenReturn(pendingBooks);
        when(bookService.getAllBooks()).thenReturn(allBooks);
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());

        String viewName = adminController.adminDashboard(model);

        assertEquals("admin/dashboard", viewName);
        verify(model).addAttribute("users", Collections.emptyList());
    }
}