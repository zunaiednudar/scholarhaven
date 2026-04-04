package com.example.scholarhaven.dto;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemResponseDTOUnitTest {

    @Test
    void testFromEntity_WithValidOrderItem() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setCoverImage("/uploads/books/test-cover.jpg");

        Order order = new Order();
        order.setId(100L);

        OrderItem item = OrderItem.builder()
                .id(10L)
                .order(order)
                .book(book)
                .quantity(2)
                .priceAtPurchase(new BigDecimal("19.99"))
                .build();

        OrderItemResponseDTO dto = OrderItemResponseDTO.fromEntity(item);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getBookId());
        assertEquals("Test Book", dto.getBookTitle());
        assertEquals("Test Author", dto.getBookAuthor());
        assertEquals("/uploads/books/test-cover.jpg", dto.getBookCoverImage());
        assertEquals(2, dto.getQuantity());
        assertEquals(new BigDecimal("19.99"), dto.getPriceAtPurchase());
        assertEquals(new BigDecimal("39.98"), dto.getSubtotal()); // 19.99 * 2
    }

    @Test
    void testFromEntity_WithNullOrderItem_ShouldReturnNull() {
        OrderItemResponseDTO dto = OrderItemResponseDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    void testFromEntity_WithBookWithoutCoverImage() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("No Cover Book");
        book.setAuthor("Anonymous");

        OrderItem item = OrderItem.builder()
                .id(20L)
                .book(book)
                .quantity(1)
                .priceAtPurchase(new BigDecimal("9.99"))
                .build();

        OrderItemResponseDTO dto = OrderItemResponseDTO.fromEntity(item);

        assertNotNull(dto);
        assertNull(dto.getBookCoverImage());
        assertEquals("No Cover Book", dto.getBookTitle());
    }

    @Test
    void testFromEntity_WithNullBook_ShouldHandleGracefully() {
        OrderItem item = OrderItem.builder()
                .id(30L)
                .book(null)
                .quantity(1)
                .priceAtPurchase(new BigDecimal("9.99"))
                .build();

        OrderItemResponseDTO dto = OrderItemResponseDTO.fromEntity(item);

        assertNotNull(dto);
        assertNull(dto.getBookId());
        assertNull(dto.getBookTitle());
        assertNull(dto.getBookAuthor());
    }

    @Test
    void testBuilder_ShouldCreateValidDTO() {
        OrderItemResponseDTO dto = OrderItemResponseDTO.builder()
                .id(1L)
                .bookId(100L)
                .bookTitle("Builder Book")
                .bookAuthor("Builder Author")
                .quantity(3)
                .priceAtPurchase(new BigDecimal("15.00"))
                .subtotal(new BigDecimal("45.00"))
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getBookId());
        assertEquals("Builder Book", dto.getBookTitle());
        assertEquals(3, dto.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();

        dto.setId(5L);
        dto.setBookId(10L);
        dto.setBookTitle("Setter Book");
        dto.setBookAuthor("Setter Author");
        dto.setBookCoverImage("/cover.jpg");
        dto.setQuantity(4);
        dto.setPriceAtPurchase(new BigDecimal("12.50"));
        dto.setSubtotal(new BigDecimal("50.00"));

        assertEquals(5L, dto.getId());
        assertEquals(10L, dto.getBookId());
        assertEquals("Setter Book", dto.getBookTitle());
        assertEquals("Setter Author", dto.getBookAuthor());
        assertEquals("/cover.jpg", dto.getBookCoverImage());
        assertEquals(4, dto.getQuantity());
        assertEquals(new BigDecimal("12.50"), dto.getPriceAtPurchase());
        assertEquals(new BigDecimal("50.00"), dto.getSubtotal());
    }
}