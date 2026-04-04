package com.example.scholarhaven.service;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.BookRepository;
import com.example.scholarhaven.strategy.book.BookStrategyContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final BookStrategyContext strategyContext;

    @Override
    public List<BookResponseDTO> getAllAvailableBooks() {
        return bookRepository.findByStatus(Book.BookStatus.AVAILABLE)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDTO getBookById(Long id) {
        Book book = findBookById(id);
        return mapToDTO(book);
    }

    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO bookRequest, User seller) {
        System.out.println("========== CREATING BOOK ==========");
        System.out.println("Seller: " + seller.getUsername());
        System.out.println("Title: " + bookRequest.getTitle());

        strategyContext.setValidationStrategyForUser(seller);

        Book book = mapToEntity(bookRequest);
        book.setSeller(seller);
        book.setStatus(Book.BookStatus.PENDING_APPROVAL);

        if (bookRequest.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(bookRequest.getCategoryId());
            book.setCategory(category);
        }

        strategyContext.validateBook(book, seller);

        if (bookRequest.getPricingStrategy() != null) {
            strategyContext.setPricingStrategy(bookRequest.getPricingStrategy());
        }

        Book savedBook = bookRepository.save(book);
        System.out.println("Book created with ID: " + savedBook.getId());
        System.out.println("==================================\n");
        return mapToDTO(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO bookRequest, User user) {
        System.out.println("========== UPDATING BOOK ==========");
        System.out.println("Book ID: " + id);
        System.out.println("User: " + user.getUsername());

        Book book = findBookById(id);

        strategyContext.setValidationStrategyForUser(user);

        if (!canUserModifyBook(user, book)) {
            throw new RuntimeException("You don't have permission to update this book");
        }

        updateBookFromDTO(book, bookRequest);

        if (bookRequest.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(bookRequest.getCategoryId());
            book.setCategory(category);
        }

        strategyContext.validateBook(book, user);

        Book updatedBook = bookRepository.save(book);
        System.out.println("Book updated successfully");
        System.out.println("==================================\n");
        return mapToDTO(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id, User user) {
        System.out.println("========== DELETE BOOK ==========");
        System.out.println("Book ID: " + id);
        System.out.println("User: " + user.getUsername() + " (ID: " + user.getId() + ")");
        System.out.println("User roles: " + user.getRoles().stream().map(r -> r.getName()).toList());

        Book book = findBookById(id);
        System.out.println("Book found: " + book.getTitle());
        System.out.println("Book seller ID: " + book.getSeller().getId());
        System.out.println("Book seller name: " + book.getSeller().getUsername());

        boolean isAdmin = user.hasRole("ADMIN");
        boolean isOwner = book.getSeller().getId().equals(user.getId());

        System.out.println("Is Admin: " + isAdmin);
        System.out.println("Is Owner: " + isOwner);

        if (!isAdmin && !isOwner) {
            String errorMsg = String.format(
                "You don't have permission to delete this book. This book belongs to user '%s' (ID: %d). Your user ID is %d.",
                book.getSeller().getUsername(), book.getSeller().getId(), user.getId()
            );
            System.out.println("Error: " + errorMsg);
            throw new RuntimeException(errorMsg);
        }

        boolean hasOrders = bookRepository.hasOrderReferences(id);
        if (hasOrders) {
            System.out.println("This book has been ordered by customers. Deleting will also remove associated orders.");
        }

        bookRepository.delete(book);
        System.out.println("Book deleted successfully" + (hasOrders ? " (associated orders removed automatically)" : ""));
        System.out.println("==========================================\n");
    }

    @Override
    @Transactional
    public BookResponseDTO markBookAsUnavailable(Long id, User user) {
        System.out.println("========== MARK BOOK UNAVAILABLE ==========");
        System.out.println("Book ID: " + id);
        System.out.println("User: " + user.getUsername());

        Book book = findBookById(id);

        boolean isAdmin = user.hasRole("ADMIN");
        boolean isOwner = book.getSeller().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You don't have permission to modify this book");
        }

        book.setStatus(Book.BookStatus.SOLD);
        Book savedBook = bookRepository.save(book);
        System.out.println("Book marked as unavailable (SOLD)");
        System.out.println("==========================================\n");

        return mapToDTO(savedBook);
    }

    @Override
    public List<BookResponseDTO> getBooksBySeller(User seller) {
        return bookRepository.findBySeller(seller)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getBooksByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return bookRepository.findByCategoryAndStatus(category, Book.BookStatus.AVAILABLE)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> searchBooks(String query) {
        return bookRepository.searchBooks(query)
                .stream()
                .map(this::mapToDTO)
                .filter(book -> "AVAILABLE".equalsIgnoreCase(book.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> searchBooksByCategory(Long categoryId, String query) {
        return bookRepository.searchBooksByCategory(categoryId, query)
                .stream()
                .map(this::mapToDTO)
                .filter(book -> "AVAILABLE".equalsIgnoreCase(book.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getRecentBooks(int limit) {
        return bookRepository.findRecentBooks(PageRequest.of(0, limit))
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getFeaturedBooks() {
        return bookRepository.findByFeaturedTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getPendingApprovalBooks() {
        return bookRepository.findPendingApprovalBooks()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookResponseDTO approveBook(Long id, User admin) {
        System.out.println("========== APPROVING BOOK ==========");
        System.out.println("Book ID: " + id);
        System.out.println("Admin: " + admin.getUsername());

        Book book = findBookById(id);

        if (!admin.hasRole("ADMIN")) {
            throw new RuntimeException("Only admins can approve books");
        }

        book.setStatus(Book.BookStatus.AVAILABLE);
        Book approvedBook = bookRepository.save(book);
        System.out.println("Book approved successfully");
        System.out.println("==================================\n");
        return mapToDTO(approvedBook);
    }

    @Override
    @Transactional
    public BookResponseDTO rejectBook(Long id, User admin) {
        System.out.println("========== REJECTING BOOK ==========");
        System.out.println("Book ID: " + id);
        System.out.println("Admin: " + admin.getUsername());

        Book book = findBookById(id);

        if (!admin.hasRole("ADMIN")) {
            throw new RuntimeException("Only admins can reject books");
        }

        book.setStatus(Book.BookStatus.REJECTED);
        Book rejectedBook = bookRepository.save(book);
        System.out.println("Book rejected");
        System.out.println("==================================\n");
        return mapToDTO(rejectedBook);
    }

    @Override
    public Map<String, String> getAvailablePricingStrategies() {
        return strategyContext.getAvailablePricingStrategies();
    }

    @Override
    @Transactional
    public BookResponseDTO applyPricingStrategy(Long bookId, String strategyName) {
        Book book = findBookById(bookId);
        strategyContext.setPricingStrategy(strategyName);
        return mapToDTO(book);
    }

    @Override
    public long getBookCountByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return bookRepository.countAvailableBooksByCategory(category);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    private boolean canUserModifyBook(User user, Book book) {
        if (user.hasRole("ADMIN")) {
            return true;
        }
        return book.getSeller().getId().equals(user.getId());
    }

    private Book mapToEntity(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        book.setStock(dto.getStock());
        book.setCoverImage(dto.getCoverImage());
        book.setPreviewPdf(dto.getPreviewPdf());
        book.setFeatured(dto.isFeatured());
        return book;
    }

    private void updateBookFromDTO(Book book, BookRequestDTO dto) {
        if (dto.getTitle() != null) book.setTitle(dto.getTitle());
        if (dto.getAuthor() != null) book.setAuthor(dto.getAuthor());
        if (dto.getDescription() != null) book.setDescription(dto.getDescription());
        if (dto.getPrice() != null) book.setPrice(dto.getPrice());
        if (dto.getStock() != null) book.setStock(dto.getStock());
        if (dto.getCoverImage() != null) book.setCoverImage(dto.getCoverImage());
        if (dto.getPreviewPdf() != null) book.setPreviewPdf(dto.getPreviewPdf());
        book.setFeatured(dto.isFeatured());
    }

    private BookResponseDTO mapToDTO(Book book) {
        BookResponseDTO dto = BookResponseDTO.fromEntity(book);

        dto.setCoverImage(normalizeFilePath(dto.getCoverImage()));
        dto.setPreviewPdf(normalizeFilePath(dto.getPreviewPdf()));

        strategyContext.setPricingStrategy("STANDARD");
        dto.setFinalPrice(strategyContext.calculatePrice(book));
        dto.setPricingStrategyUsed(strategyContext.getCurrentPricingStrategyName());

        return dto;
    }

    private String normalizeFilePath(String path) {
        if (path == null || path.isBlank()) return null;
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("/")) return path;
        return "/" + path;
    }
}