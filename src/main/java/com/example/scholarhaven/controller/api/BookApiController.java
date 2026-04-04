package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.CategoryService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
    
public class BookApiController {

    private final BookService bookService;
    private final UserService userService;
    private final CategoryService categoryService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // ========== PUBLIC ENDPOINTS ==========

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

    @GetMapping("/strategies")
    public ResponseEntity<Map<String, String>> getAvailablePricingStrategies() {
        return ResponseEntity.ok(bookService.getAvailablePricingStrategies());
    }

    // ========== CATEGORIES ENDPOINT ==========

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        System.out.println("📚 API /api/books/categories called - returning " + categories.size() + " categories");
        return ResponseEntity.ok(categories);
    }

    // ========== SELLER ENDPOINTS ==========

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequest,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByUsername(userDetails.getUsername());
        BookResponseDTO createdBook = bookService.createBook(bookRequest, seller);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<BookResponseDTO> createBookWithImage(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "featured", defaultValue = "false") boolean featured,
            @RequestParam(value = "pricingStrategy", required = false) String pricingStrategy,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "previewPdf", required = false) MultipartFile previewPdf,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User seller = userService.findByUsername(userDetails.getUsername());

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle(title);
        bookRequest.setAuthor(author);
        bookRequest.setDescription(description);
        bookRequest.setPrice(price);
        bookRequest.setStock(stock);
        bookRequest.setCategoryId(categoryId);
        bookRequest.setFeatured(featured);
        bookRequest.setPricingStrategy(pricingStrategy);

        // Handle image upload
        if (coverImage != null && !coverImage.isEmpty()) {
            String fileName = saveFile(coverImage, "books");
            bookRequest.setCoverImage("/uploads/books/" + fileName);
        }

        // Handle preview PDF upload
        if (previewPdf != null && !previewPdf.isEmpty()) {
            String pdfName = saveFile(previewPdf, "previews");
            bookRequest.setPreviewPdf("/uploads/previews/" + pdfName);
            System.out.println("Uploaded preview PDF file saved: " + bookRequest.getPreviewPdf());
        }

        BookResponseDTO createdBook = bookService.createBook(bookRequest, seller);
        System.out.println("Created book with coverImage=" + bookRequest.getCoverImage() + " previewPdf=" + bookRequest.getPreviewPdf());
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

    @PutMapping(value = "/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<BookResponseDTO> updateBookWithImage(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "featured", defaultValue = "false") boolean featured,
            @RequestParam(value = "pricingStrategy", required = false) String pricingStrategy,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "previewPdf", required = false) MultipartFile previewPdf,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User user = userService.findByUsername(userDetails.getUsername());

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle(title);
        bookRequest.setAuthor(author);
        bookRequest.setDescription(description);
        bookRequest.setPrice(price);
        bookRequest.setStock(stock);
        bookRequest.setCategoryId(categoryId);
        bookRequest.setFeatured(featured);
        bookRequest.setPricingStrategy(pricingStrategy);

        // Handle image upload
        if (coverImage != null && !coverImage.isEmpty()) {
            String fileName = saveFile(coverImage, "books");
            bookRequest.setCoverImage("/uploads/books/" + fileName);
        }

        // Handle preview PDF upload
        if (previewPdf != null && !previewPdf.isEmpty()) {
            String pdfName = saveFile(previewPdf, "previews");
            bookRequest.setPreviewPdf("/uploads/previews/" + pdfName);
            System.out.println("Updated preview PDF file saved: " + bookRequest.getPreviewPdf());
        }

        BookResponseDTO updatedBook = bookService.updateBook(id, bookRequest, user);
        System.out.println("Updated book with coverImage=" + bookRequest.getCoverImage() + " previewPdf=" + bookRequest.getPreviewPdf());
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== DELETE BOOK REQUEST ==========");
        System.out.println("Book ID: " + id);
        
        if (userDetails == null) {
            System.out.println("❌ User not authenticated");
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            System.out.println("User: " + user.getUsername() + " (ID: " + user.getId() + ")");
            System.out.println("User roles: " + user.getRoles().stream().map(r -> r.getName()).toList());
            
            bookService.deleteBook(id, user);
            System.out.println("✅ Book deleted successfully");
            System.out.println("==========================================\n");
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully. Any associated orders have been removed."));
            
        } catch (RuntimeException e) {
            System.out.println("❌ Error: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/seller/me")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<List<BookResponseDTO>> getMyBooks(@AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(bookService.getBooksBySeller(seller));
    }

    @PostMapping("/{id}/apply-strategy")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<BookResponseDTO> applyPricingStrategy(@PathVariable Long id,
                                                                @RequestParam String strategyName) {
        BookResponseDTO book = bookService.applyPricingStrategy(id, strategyName);
        return ResponseEntity.ok(book);
    }

    // ========== ADMIN ENDPOINTS ==========

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

    // ========== HELPER METHODS ==========

    private String saveFile(MultipartFile file, String folder) throws IOException {
        Path uploadPath = Paths.get(uploadDir, folder);
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return fileName;
    }
}
