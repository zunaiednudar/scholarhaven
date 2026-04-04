
package com.example.scholarhaven.service;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.BookRepository;
import com.example.scholarhaven.repository.CategoryRepository;
import com.example.scholarhaven.repository.RoleRepository;
import com.example.scholarhaven.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testSeller;
    private User testAdmin;
    private Category testCategory;
    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        Role sellerRole = roleRepository.findByName("SELLER").orElseGet(() -> {
            Role role = new Role();
            role.setName("SELLER");
            return roleRepository.save(role);
        });

        testSeller = new User();
        testSeller.setName("Test Seller");
        testSeller.setUsername("seller_" + System.currentTimeMillis());
        testSeller.setEmail("seller_" + System.currentTimeMillis() + "@test.com");
        testSeller.setPassword("password");
        testSeller.setEnabled(true);
        Set<Role> sellerRoles = new HashSet<>();
        sellerRoles.add(sellerRole);
        testSeller.setRoles(sellerRoles);
        testSeller = userRepository.save(testSeller);

        testAdmin = new User();
        testAdmin.setName("Test Admin");
        testAdmin.setUsername("admin_" + System.currentTimeMillis());
        testAdmin.setEmail("admin_" + System.currentTimeMillis() + "@test.com");
        testAdmin.setPassword("password");
        testAdmin.setEnabled(true);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        testAdmin.setRoles(adminRoles);
        testAdmin = userRepository.save(testAdmin);

        testCategory = new Category();
        testCategory.setName("Fiction_" + System.currentTimeMillis());
        testCategory = categoryRepository.save(testCategory);

        testBook = new Book();
        testBook.setTitle("Service Integration Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPrice(new BigDecimal("49.99"));
        testBook.setStock(20);
        testBook.setStatus(Book.BookStatus.AVAILABLE);
        testBook.setSeller(testSeller);
        testBook.setCategory(testCategory);
        testBook = bookRepository.save(testBook);
    }

    @Test
    void testGetBookById() {
        BookResponseDTO found = bookService.getBookById(testBook.getId());
        assertNotNull(found);
        assertEquals("Service Integration Test Book", found.getTitle());
    }

    @Test
    void testGetAllBooks() {
        List<BookResponseDTO> books = bookService.getAllBooks();
        assertFalse(books.isEmpty());
        assertTrue(books.size() >= 1);
    }

    @Test
    void testGetAllAvailableBooks() {
        List<BookResponseDTO> books = bookService.getAllAvailableBooks();
        assertFalse(books.isEmpty());
    }

    @Test
    void testGetBooksBySeller() {
        List<BookResponseDTO> books = bookService.getBooksBySeller(testSeller);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
    }

    @Test
    void testGetBooksByCategory() {
        List<BookResponseDTO> booksInTestCategory = bookService.getBooksByCategory(testCategory.getId());
        assertFalse(booksInTestCategory.isEmpty(), "Should find books in the test category");
        assertEquals(1, booksInTestCategory.size(), "Should find exactly 1 book in the test category");

        Category otherCategory = new Category();
        otherCategory.setName("Other_" + System.currentTimeMillis());
        otherCategory = categoryRepository.save(otherCategory);

        List<BookResponseDTO> booksInOtherCategory = bookService.getBooksByCategory(otherCategory.getId());
        assertTrue(booksInOtherCategory.isEmpty(), "Should find no books in the other category");
    }

    @Test
    void testSearchBooks() {
        List<BookResponseDTO> foundBooks = bookService.searchBooks("Service");
        assertFalse(foundBooks.isEmpty(), "Should find books with 'Service' in title");
        assertEquals(1, foundBooks.size(), "Should find exactly 1 book with 'Service' in title");

        List<BookResponseDTO> authorBooks = bookService.searchBooks("Test Author");
        assertFalse(authorBooks.isEmpty(), "Should find books with 'Test Author' as author");
        assertEquals(1, authorBooks.size(), "Should find exactly 1 book by 'Test Author'");

        List<BookResponseDTO> notFoundBooks = bookService.searchBooks("NonExistentTermXYZ");
        assertTrue(notFoundBooks.isEmpty(), "Should find no books with non-existent term");
    }

    @Test
    void testCreateBook() {
        BookRequestDTO request = new BookRequestDTO();
        request.setTitle("Created Book");
        request.setAuthor("Created Author");
        request.setPrice(new BigDecimal("59.99"));
        request.setStock(25);
        request.setCategoryId(testCategory.getId());
        request.setPricingStrategy("STANDARD");

        BookResponseDTO created = bookService.createBook(request, testSeller);

        assertNotNull(created);
        assertEquals("Created Book", created.getTitle());
        assertNotNull(created.getId());
    }

    @Test
    void testUpdateBook() {
        BookRequestDTO updateRequest = new BookRequestDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setAuthor("Updated Author");
        updateRequest.setPrice(new BigDecimal("69.99"));
        updateRequest.setStock(30);
        updateRequest.setCategoryId(testCategory.getId());

        BookResponseDTO updated = bookService.updateBook(testBook.getId(), updateRequest, testSeller);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Author", updated.getAuthor());
        assertEquals(new BigDecimal("69.99"), updated.getOriginalPrice());
    }

    @Test
    void testDeleteBook() {
        bookService.deleteBook(testBook.getId(), testSeller);

        assertThrows(RuntimeException.class, () -> bookService.getBookById(testBook.getId()));
    }

    @Test
    void testApproveBook() {
        testBook.setStatus(Book.BookStatus.PENDING_APPROVAL);
        bookRepository.save(testBook);

        BookResponseDTO approved = bookService.approveBook(testBook.getId(), testAdmin);
        assertEquals("AVAILABLE", approved.getStatus());

        assertThrows(RuntimeException.class, () -> 
            bookService.approveBook(testBook.getId(), testSeller)
        );
    }

    @Test
    void testRejectBook() {
        testBook.setStatus(Book.BookStatus.PENDING_APPROVAL);
        bookRepository.save(testBook);

        BookResponseDTO rejected = bookService.rejectBook(testBook.getId(), testAdmin);
        assertEquals("REJECTED", rejected.getStatus());

        assertThrows(RuntimeException.class, () -> 
            bookService.rejectBook(testBook.getId(), testSeller)
        );
    }

    @Test
    void testGetFeaturedBooks() {
        testBook.setFeatured(true);
        bookRepository.save(testBook);

        List<BookResponseDTO> featured = bookService.getFeaturedBooks();
        assertFalse(featured.isEmpty());
        assertEquals(1, featured.size());
    }

    @Test
    void testGetPendingApprovalBooks() {
        testBook.setStatus(Book.BookStatus.PENDING_APPROVAL);
        bookRepository.save(testBook);

        List<BookResponseDTO> pending = bookService.getPendingApprovalBooks();
        assertFalse(pending.isEmpty());
        assertEquals(1, pending.size());

        testBook.setStatus(Book.BookStatus.AVAILABLE);
        bookRepository.save(testBook);

        pending = bookService.getPendingApprovalBooks();
        assertTrue(pending.isEmpty() || pending.size() == 0);
    }

    @Test
    void testGetBookCountByCategory() {
        long count = bookService.getBookCountByCategory(testCategory.getId());
        assertEquals(1, count, "Should have exactly 1 book in the test category");

        Category emptyCategory = new Category();
        emptyCategory.setName("Empty_" + System.currentTimeMillis());
        emptyCategory = categoryRepository.save(emptyCategory);

        long emptyCount = bookService.getBookCountByCategory(emptyCategory.getId());
        assertEquals(0, emptyCount, "Should have 0 books in the empty category");
    }
}
