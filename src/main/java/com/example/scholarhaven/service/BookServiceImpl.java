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
    public BookResponseDTO getBookById(Long id) {
        Book book = findBookById(id);
        return mapToDTO(book);
    }

    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO bookRequest, User seller) {
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
        return mapToDTO(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO bookRequest, User user) {
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
        return mapToDTO(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id, User user) {
        Book book = findBookById(id);

        if (!canUserModifyBook(user, book)) {
            throw new RuntimeException("You don't have permission to delete this book");
        }

        bookRepository.delete(book);
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
        Book book = findBookById(id);

        if (!"ADMIN".equals(admin.getRole().getName())) {
            throw new RuntimeException("Only admins can approve books");
        }

        book.setStatus(Book.BookStatus.AVAILABLE);
        Book approvedBook = bookRepository.save(book);
        return mapToDTO(approvedBook);
    }

    @Override
    @Transactional
    public BookResponseDTO rejectBook(Long id, User admin) {
        Book book = findBookById(id);

        if (!"ADMIN".equals(admin.getRole().getName())) {
            throw new RuntimeException("Only admins can reject books");
        }

        book.setStatus(Book.BookStatus.REJECTED);
        Book rejectedBook = bookRepository.save(book);
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
        if ("ADMIN".equals(user.getRole().getName())) {
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
        book.setFeatured(dto.isFeatured());
    }

    private BookResponseDTO mapToDTO(Book book) {
        BookResponseDTO dto = BookResponseDTO.fromEntity(book);

        strategyContext.setPricingStrategy("STANDARD");
        dto.setFinalPrice(strategyContext.calculatePrice(book));
        dto.setPricingStrategyUsed(strategyContext.getCurrentPricingStrategyName());

        return dto;
    }
}