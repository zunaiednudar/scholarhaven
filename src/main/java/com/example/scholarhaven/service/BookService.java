package com.example.scholarhaven.service;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.User;
import java.util.List;
import java.util.Map;

public interface BookService {
    List<BookResponseDTO> getAllAvailableBooks();
    List<BookResponseDTO> getAllBooks();
    BookResponseDTO getBookById(Long id);
    BookResponseDTO createBook(BookRequestDTO bookRequest, User seller);
    BookResponseDTO updateBook(Long id, BookRequestDTO bookRequest, User user);
    void deleteBook(Long id, User user);
    BookResponseDTO markBookAsUnavailable(Long id, User user);
    List<BookResponseDTO> getBooksBySeller(User seller);
    List<BookResponseDTO> getBooksByCategory(Long categoryId);
    List<BookResponseDTO> searchBooks(String query);
    List<BookResponseDTO> searchBooksByCategory(Long categoryId, String query);
    List<BookResponseDTO> getRecentBooks(int limit);
    List<BookResponseDTO> getFeaturedBooks();
    List<BookResponseDTO> getPendingApprovalBooks();
    BookResponseDTO approveBook(Long id, User admin);
    BookResponseDTO rejectBook(Long id, User admin);
    Map<String, String> getAvailablePricingStrategies();
    BookResponseDTO applyPricingStrategy(Long bookId, String strategyName);
    long getBookCountByCategory(Long categoryId);
}