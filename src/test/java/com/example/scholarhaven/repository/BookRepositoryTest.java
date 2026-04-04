```java
package com.example.scholarhaven.repository;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testSeller;
    private Category testCategory;
    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        testSeller = new User();
        testSeller.setName("Test Seller");
        testSeller.setUsername("seller_" + System.currentTimeMillis());
        testSeller.setEmail("seller_" + System.currentTimeMillis() + "@test.com");
        testSeller.setPassword("password");
        testSeller.setEnabled(true);
        testSeller = userRepository.save(testSeller);

        testCategory = new Category();
        testCategory.setName("Fiction_" + System.currentTimeMillis());
        testCategory = categoryRepository.save(testCategory);

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPrice(new BigDecimal("39.99"));
        testBook.setStock(15);
        testBook.setStatus(Book.BookStatus.AVAILABLE);
        testBook.setSeller(testSeller);
        testBook.setCategory(testCategory);
        testBook = bookRepository.save(testBook);
    }

    @Test
    void testFindById() {
        Book found = bookRepository.findById(testBook.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Test Book", found.getTitle());
    }

    @Test
    void testFindByStatus() {
        List<Book> books = bookRepository.findByStatus(Book.BookStatus.AVAILABLE);
        assertFalse(books.isEmpty());
    }

    @Test
    void testFindBySeller() {
        List<Book> books = bookRepository.findBySeller(testSeller);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
    }

    @Test
    void testFindByCategory() {
        List<Book> books = bookRepository.findByCategory(testCategory);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
    }
}