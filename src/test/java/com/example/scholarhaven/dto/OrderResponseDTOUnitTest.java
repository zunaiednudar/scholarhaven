package com.example.scholarhaven.dto;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.entity.OrderItem;
import com.example.scholarhaven.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseDTOUnitTest {

    @Test
    void testFromEntity_WithCompleteOrder() {
        User buyer = new User();
        buyer.setId(10L);
        buyer.setUsername("buyer123");
        buyer.setName("John Buyer");

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setCoverImage("/cover1.jpg");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");

        OrderItem item1 = OrderItem.builder()
                .id(101L)
                .book(book1)
                .quantity(2)
                .priceAtPurchase(new BigDecimal("19.99"))
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(102L)
                .book(book2)
                .quantity(1)
                .priceAtPurchase(new BigDecimal("29.99"))
                .build();

        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Order order = Order.builder()
                .id(1000L)
                .buyer(buyer)
                .items(items)
                .totalPrice(new BigDecimal("69.97"))
                .status(Order.OrderStatus.CONFIRMED)
                .createdAt(LocalDateTime.of(2024, 2, 20, 14, 30))
                .build();

        OrderResponseDTO dto = OrderResponseDTO.fromEntity(order);

        assertEquals(1000L, dto.getId());
        assertEquals(10L, dto.getBuyerId());
        assertEquals("buyer123", dto.getBuyerUsername());
        assertEquals("John Buyer", dto.getBuyerName());
        assertEquals(new BigDecimal("69.97"), dto.getTotalPrice());
        assertEquals("CONFIRMED", dto.getStatus());
        assertNotNull(dto.getCreatedAt());

        assertNotNull(dto.getItems());
        assertEquals(2, dto.getItems().size());

        OrderItemResponseDTO itemDto1 = dto.getItems().get(0);
        assertEquals(101L, itemDto1.getId());
        assertEquals(1L, itemDto1.getBookId());
        assertEquals("Book 1", itemDto1.getBookTitle());
        assertEquals(2, itemDto1.getQuantity());
        assertEquals(new BigDecimal("19.99"), itemDto1.getPriceAtPurchase());
        assertEquals("/cover1.jpg", itemDto1.getBookCoverImage());

        OrderItemResponseDTO itemDto2 = dto.getItems().get(1);
        assertEquals(102L, itemDto2.getId());
        assertEquals(2L, itemDto2.getBookId());
        assertEquals("Book 2", itemDto2.getBookTitle());
        assertEquals(1, itemDto2.getQuantity());
        assertNull(itemDto2.getBookCoverImage());
    }

    @Test
    void testFromEntity_WithNullOrder_ShouldReturnNull() {
        OrderResponseDTO dto = OrderResponseDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    void testFromEntity_WithEmptyItemsList() {
        User buyer = new User();
        buyer.setId(5L);
        buyer.setUsername("empty");

        Order order = Order.builder()
                .id(2000L)
                .buyer(buyer)
                .items(new ArrayList<>())
                .totalPrice(new BigDecimal("0.00"))
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        OrderResponseDTO dto = OrderResponseDTO.fromEntity(order);

        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void testFromEntity_WithNullBuyer() {
        Order order = Order.builder()
                .id(3000L)
                .buyer(null)
                .items(new ArrayList<>())
                .totalPrice(new BigDecimal("0.00"))
                .status(Order.OrderStatus.PENDING)
                .build();

        OrderResponseDTO dto = OrderResponseDTO.fromEntity(order);

        assertNull(dto.getBuyerId());
        assertNull(dto.getBuyerUsername());
        assertNull(dto.getBuyerName());
    }

    @Test
    void testBuilder_ShouldCreateValidDTO() {
        OrderItemResponseDTO itemDto = OrderItemResponseDTO.builder()
                .id(1L)
                .bookTitle("Builder Book")
                .quantity(1)
                .build();

        OrderResponseDTO dto = OrderResponseDTO.builder()
                .id(5000L)
                .buyerId(99L)
                .buyerUsername("builder")
                .items(List.of(itemDto))
                .totalPrice(new BigDecimal("29.99"))
                .status("DELIVERED")
                .build();

        assertEquals(5000L, dto.getId());
        assertEquals(99L, dto.getBuyerId());
        assertEquals("builder", dto.getBuyerUsername());
        assertEquals(1, dto.getItems().size());
        assertEquals(new BigDecimal("29.99"), dto.getTotalPrice());
        assertEquals("DELIVERED", dto.getStatus());
    }
}