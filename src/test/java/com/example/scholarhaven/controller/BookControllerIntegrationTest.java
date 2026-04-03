package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.BookRequestDTO;
import com.example.scholarhaven.entity.*;
import com.example.scholarhaven.repository.*;
import com.example.scholarhaven.security.JwtService;
import com.example.scholarhaven.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private com.example.scholarhaven.service.BookService bookService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private User testSeller;
    private User testAdmin;
    private Category testCategory;
    private Book testBook;
    private String userToken;
    private String sellerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Clear test data
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create roles
        Role buyerRole = createRoleIfNotFound("BUYER");
        Role sellerRole = createRoleIfNotFound("SELLER");
        Role adminRole = createRoleIfNotFound("ADMIN");

        // Create test user (BUYER only)
        testUser = createUser("Test Buyer", "testbuyer", "testbuyer@test.com", "password", Set.of(buyerRole));
        userToken = jwtService.generateToken(new UserPrincipal(testUser));

        // Create seller user
        testSeller = createUser("Test Seller", "testseller", "testseller@test.com", "password", Set.of(buyerRole, sellerRole));
        sellerToken = jwtService.generateToken(new UserPrincipal(testSeller));

        // Create admin user
        testAdmin = createUser("Test Admin", "testadmin", "testadmin@test.com", "password", Set.of(adminRole));
        adminToken = jwtService.generateToken(new UserPrincipal(testAdmin));

        // Create test category
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory = categoryRepository.save(testCategory);

        // Create test book
        testBook = createTestBook("Integration Test Book", "Test Author", new BigDecimal("29.99"), 10, 
                                   Book.BookStatus.AVAILABLE, testSeller, testCategory, true);
    }

    private Role createRoleIfNotFound(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }

    private User createUser(String name, String username, String email, String password, Set<Role> roles) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private Book createTestBook(String title, String author, BigDecimal price, int stock, 
                                 Book.BookStatus status, User seller, Category category, boolean featured) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setStock(stock);
        book.setStatus(status);
        book.setSeller(seller);
        book.setCategory(category);
        book.setFeatured(featured);
        return bookRepository.save(book);
    }

    // ========== PUBLIC PAGE TESTS ==========

    @Test
    void testListBooks_AllBooks_ShouldReturnBooksPage() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("bookCounts"));
    }

    @Test
    void testListBooks_WithSearchQuery_ShouldReturnFilteredBooks() throws Exception {
        mockMvc.perform(get("/books").param("q", "Integration"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"));
    }

    @Test
    void testListBooks_WithCategory_ShouldReturnCategoryFilteredBooks() throws Exception {
        mockMvc.perform(get("/books").param("categoryId", testCategory.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("books"));
    }

    @Test
    void testViewBook_ExistingBook_ShouldReturnBookDetailPage() throws Exception {
        mockMvc.perform(get("/books/{id}", testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("book-detail"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void testMyBooksDashboard_AuthenticatedSeller_ShouldReturnDashboardPage() throws Exception {
        mockMvc.perform(get("/my-books-dashboard")
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(view().name("my-books-dashboard"));
    }

    // ========== API ENDPOINT TESTS ==========

    @Test
    void testApiGetAllBooks_ShouldReturnBooksList() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiGetBookById_ExistingBook_ShouldReturnBook() throws Exception {
        mockMvc.perform(get("/api/books/{id}", testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testBook.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Integration Test Book")));
    }

    @Test
    void testApiSearchBooks_WithQuery_ShouldReturnMatchingBooks() throws Exception {
        mockMvc.perform(get("/api/books/search").param("q", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiGetBooksByCategory_ShouldReturnCategoryBooks() throws Exception {
        mockMvc.perform(get("/api/books/category/{categoryId}", testCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiGetFeaturedBooks_ShouldReturnFeaturedBooks() throws Exception {
        mockMvc.perform(get("/api/books/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiGetRecentBooks_ShouldReturnRecentBooks() throws Exception {
        mockMvc.perform(get("/api/books/recent").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiGetCategories_ShouldReturnCategoriesList() throws Exception {
        mockMvc.perform(get("/api/books/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiGetStrategies_ShouldReturnPricingStrategies() throws Exception {
        mockMvc.perform(get("/api/books/strategies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.STANDARD", is("Standard Price")));
    }

    @Test
    void testApiGetSellerBooks_AuthenticatedSeller_ShouldReturnBooks() throws Exception {
        mockMvc.perform(get("/api/books/seller/me")
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiCreateBook_Seller_ShouldCreateBook() throws Exception {
        BookRequestDTO newBook = new BookRequestDTO();
        newBook.setTitle("New API Book");
        newBook.setAuthor("New Author");
        newBook.setDescription("A brand new book");
        newBook.setPrice(new BigDecimal("39.99"));
        newBook.setStock(5);
        newBook.setCategoryId(testCategory.getId());
        newBook.setPricingStrategy("STANDARD");
        newBook.setFeatured(false);

        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New API Book")));
    }

    @Test
    void testApiUpdateBook_Owner_ShouldUpdateBook() throws Exception {
        BookRequestDTO updateRequest = new BookRequestDTO();
        updateRequest.setTitle("Updated API Book");
        updateRequest.setAuthor("Updated Author");
        updateRequest.setPrice(new BigDecimal("49.99"));
        updateRequest.setStock(8);
        updateRequest.setCategoryId(testCategory.getId());

        mockMvc.perform(put("/api/books/{id}", testBook.getId())
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated API Book")));
    }

    @Test
    void testApiDeleteBook_Owner_ShouldDeleteBook() throws Exception {
        // Create a new book just for deletion test
        Book bookToDelete = createTestBook("Book to Delete", "Delete Author", new BigDecimal("19.99"), 5,
                                           Book.BookStatus.AVAILABLE, testSeller, testCategory, false);

        mockMvc.perform(delete("/api/books/{id}", bookToDelete.getId())
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testApiApplyPricingStrategy_ShouldReturnBookWithUpdatedPrice() throws Exception {
        mockMvc.perform(post("/api/books/{id}/apply-strategy", testBook.getId())
                .param("strategyName", "STANDARD")
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testApiGetPendingApprovalBooks_Admin_ShouldReturnPendingBooks() throws Exception {
        // Create a pending approval book
        Book pendingBook = createTestBook("Pending Book", "Pending Author", new BigDecimal("15.99"), 2,
                                          Book.BookStatus.PENDING_APPROVAL, testSeller, testCategory, false);

        mockMvc.perform(get("/api/books/admin/pending")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testApiApproveBook_Admin_ShouldApproveBook() throws Exception {
        // Create a pending approval book
        Book pendingBook = createTestBook("Pending for Approval", "Approve Author", new BigDecimal("25.99"), 10,
                                          Book.BookStatus.PENDING_APPROVAL, testSeller, testCategory, false);

        mockMvc.perform(post("/api/books/admin/{id}/approve", pendingBook.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AVAILABLE")));
    }

    @Test
    void testApiRejectBook_Admin_ShouldRejectBook() throws Exception {
        // Create a pending approval book
        Book pendingBook = createTestBook("Pending for Rejection", "Reject Author", new BigDecimal("25.99"), 10,
                                          Book.BookStatus.PENDING_APPROVAL, testSeller, testCategory, false);

        mockMvc.perform(post("/api/books/admin/{id}/reject", pendingBook.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }
}