package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.OrderItemRequestDTO;
import com.example.scholarhaven.dto.OrderRequestDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

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
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testBuyer;
    private User testSeller;
    private User testAdmin;
    private Category testCategory;
    private Book testBook1;
    private Book testBook2;
    private Order testOrder;
    private String buyerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        Role buyerRole = createRoleIfNotFound("BUYER");
        Role sellerRole = createRoleIfNotFound("SELLER");
        Role adminRole = createRoleIfNotFound("ADMIN");

        testBuyer = createUser("Test Buyer", "testbuyer", "buyer@test.com", "password", Set.of(buyerRole));
        buyerToken = jwtService.generateToken(new UserPrincipal(testBuyer));

        testSeller = createUser("Test Seller", "testseller", "seller@test.com", "password", Set.of(buyerRole, sellerRole));

        testAdmin = createUser("Test Admin", "testadmin", "admin@test.com", "password", Set.of(adminRole));
        adminToken = jwtService.generateToken(new UserPrincipal(testAdmin));

        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory = categoryRepository.save(testCategory);

        testBook1 = createTestBook("Test Book 1", "Author 1", new BigDecimal("19.99"), 10, 
                                    Book.BookStatus.AVAILABLE, testSeller, testCategory);
        testBook2 = createTestBook("Test Book 2", "Author 2", new BigDecimal("29.99"), 5, 
                                    Book.BookStatus.AVAILABLE, testSeller, testCategory);

        testOrder = createOrder(testBuyer, new BigDecimal("49.98"), Order.OrderStatus.PENDING);
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
                                 Book.BookStatus status, User seller, Category category) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setStock(stock);
        book.setStatus(status);
        book.setSeller(seller);
        book.setCategory(category);
        return bookRepository.save(book);
    }

    private Order createOrder(User buyer, BigDecimal totalPrice, Order.OrderStatus status) {
        Order order = new Order();
        order.setBuyer(buyer);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Test
    void testMyOrders_AuthenticatedUser_ShouldReturnOrdersPage() throws Exception {
        mockMvc.perform(get("/orders")
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"));
    }

    @Test
    void testOrderDetail_OwnOrder_ShouldReturnOrderDetailPage() throws Exception {
        mockMvc.perform(get("/orders/{id}", testOrder.getId())
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(view().name("order-detail"));
    }

    @Test
    void testApiCreateOrder_ValidRequest_ShouldCreateOrder() throws Exception {
        OrderItemRequestDTO item1 = new OrderItemRequestDTO();
        item1.setBookId(testBook1.getId());
        item1.setQuantity(2);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setItems(List.of(item1));

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void testApiCreateOrder_EmptyCart_ShouldReturnBadRequest() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setItems(List.of());

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testApiGetMyOrders_ShouldReturnBuyerOrders() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders")
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testApiGetOrderById_OwnOrder_ShouldReturnOrder() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", testOrder.getId())
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testOrder.getId().intValue())));
    }

    @Test
    void testApiCancelOrder_OwnPendingOrder_ShouldCancel() throws Exception {
        mockMvc.perform(post("/api/orders/{id}/cancel", testOrder.getId())
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testApiGetAllOrders_Admin_ShouldReturnAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders/admin/all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testApiGetOrdersByStatus_Admin_ShouldReturnFilteredOrders() throws Exception {
        mockMvc.perform(get("/api/orders/admin/status/PENDING")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testApiUpdateOrderStatus_Admin_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(put("/api/orders/admin/{id}/status", testOrder.getId())
                .param("status", "CONFIRMED")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }
}