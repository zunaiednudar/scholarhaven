package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookApiController {

    private final BookService bookService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllAvailableBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(@RequestParam String q) {
        return ResponseEntity.ok(bookService.searchBooks(q));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookResponseDTO>> getBooksByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(bookService.getBooksByCategory(categoryId));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<BookResponseDTO>> getFeaturedBooks() {
        return ResponseEntity.ok(bookService.getFeaturedBooks());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<BookResponseDTO>> getRecentBooks(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(bookService.getRecentBooks(limit));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequest,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByUsername(userDetails.getUsername());
        BookResponseDTO createdBook = bookService.createBook(bookRequest, seller);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id,
                                                      @RequestBody BookRequestDTO bookRequest,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        BookResponseDTO updatedBook = bookService.updateBook(id, bookRequest, user);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        bookService.deleteBook(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/seller/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<BookResponseDTO>> getMyBooks(@AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(bookService.getBooksBySeller(seller));
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookResponseDTO>> getPendingApprovalBooks() {
        return ResponseEntity.ok(bookService.getPendingApprovalBooks());
    }

    @PostMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> approveBook(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userService.findByUsername(userDetails.getUsername());
        BookResponseDTO approvedBook = bookService.approveBook(id, admin);
        return ResponseEntity.ok(approvedBook);
    }

    @PostMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> rejectBook(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userService.findByUsername(userDetails.getUsername());
        BookResponseDTO rejectedBook = bookService.rejectBook(id, admin);
        return ResponseEntity.ok(rejectedBook);
    }

    @GetMapping("/strategies")
    public ResponseEntity<Map<String, String>> getAvailablePricingStrategies() {
        return ResponseEntity.ok(bookService.getAvailablePricingStrategies());
    }

    @PostMapping("/{id}/apply-strategy")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<BookResponseDTO> applyPricingStrategy(@PathVariable Long id,
                                                                @RequestParam String strategyName) {
        BookResponseDTO book = bookService.applyPricingStrategy(id, strategyName);
        return ResponseEntity.ok(book);
    }
}