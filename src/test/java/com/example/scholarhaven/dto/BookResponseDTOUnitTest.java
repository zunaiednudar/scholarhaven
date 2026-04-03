package com.example.scholarhaven.dto;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookResponseDTOUnitTest {

    @Test
    void testFromEntity_WithCompleteBook() {
        User seller = new User();
        seller.setId(10L);
        seller.setUsername("seller123");

        Category category = new Category();
        category.setId(5L);
        category.setName("Technology");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Spring Boot Guide");
        book.setAuthor("John Doe");
        book.setDescription("A comprehensive guide to Spring Boot");
        book.setPrice(new BigDecimal("49.99"));
        book.setStock(15);
        book.setStatus(Book.BookStatus.AVAILABLE);
        book.setCoverImage("/uploads/books/cover.jpg");
        book.setPreviewPdf("/uploads/previews/preview.pdf");
        book.setFeatured(true);
        book.setSeller(seller);
        book.setCategory(category);
        book.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

        BookResponseDTO dto = BookResponseDTO.fromEntity(book);

        assertEquals(1L, dto.getId());
        assertEquals("Spring Boot Guide", dto.getTitle());
        assertEquals("John Doe", dto.getAuthor());
        assertEquals("A comprehensive guide to Spring Boot", dto.getDescription());
        assertEquals(new BigDecimal("49.99"), dto.getOriginalPrice());
        assertEquals(15, dto.getStock());
        assertEquals("AVAILABLE", dto.getStatus());
        assertEquals("/uploads/books/cover.jpg", dto.getCoverImage());
        assertEquals("/uploads/previews/preview.pdf", dto.getPreviewPdf());
        assertTrue(dto.isFeatured());
        assertEquals("seller123", dto.getSellerName());
        assertEquals(10L, dto.getSellerId());
        assertEquals(5L, dto.getCategoryId());
        assertEquals("Technology", dto.getCategoryName());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    void testFromEntity_WithMinimalBook() {
        User seller = new User();
        seller.setId(1L);
        seller.setUsername("minimal");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Minimal Book");
        book.setAuthor("Minimal Author");
        book.setPrice(new BigDecimal("9.99"));
        book.setStock(1);
        book.setSeller(seller);

        BookResponseDTO dto = BookResponseDTO.fromEntity(book);

        assertEquals(2L, dto.getId());
        assertEquals("Minimal Book", dto.getTitle());
        assertEquals("Minimal Author", dto.getAuthor());
        assertEquals(new BigDecimal("9.99"), dto.getOriginalPrice());
        assertEquals(1, dto.getStock());
        assertNull(dto.getDescription());
        assertNull(dto.getCoverImage());
        assertNull(dto.getPreviewPdf());
        assertFalse(dto.isFeatured());
    }

    @Test
    void testFromEntity_WithNullSellerAndCategory() {
        Book book = new Book();
        book.setId(3L);
        book.setTitle("No Seller Book");
        book.setAuthor("Unknown");
        book.setPrice(new BigDecimal("0.00"));
        book.setStock(0);
        book.setSeller(null);
        book.setCategory(null);

        BookResponseDTO dto = BookResponseDTO.fromEntity(book);

        assertNull(dto.getSellerName());
        assertNull(dto.getSellerId());
        assertNull(dto.getCategoryId());
        assertNull(dto.getCategoryName());
    }

    @Test
    void testSettersAndGetters() {
        BookResponseDTO dto = new BookResponseDTO();

        dto.setId(100L);
        dto.setTitle("Setter Title");
        dto.setAuthor("Setter Author");
        dto.setDescription("Setter Description");
        dto.setOriginalPrice(new BigDecimal("25.00"));
        dto.setFinalPrice(new BigDecimal("22.50"));
        dto.setStock(50);
        dto.setStatus("AVAILABLE");
        dto.setCoverImage("/cover.jpg");
        dto.setPreviewPdf("/preview.pdf");
        dto.setFeatured(true);
        dto.setSellerName("seller");
        dto.setSellerId(200L);
        dto.setCategoryId(300L);
        dto.setCategoryName("Fiction");
        dto.setPricingStrategyUsed("DISCOUNT");

        assertEquals(100L, dto.getId());
        assertEquals("Setter Title", dto.getTitle());
        assertEquals("Setter Author", dto.getAuthor());
        assertEquals("Setter Description", dto.getDescription());
        assertEquals(new BigDecimal("25.00"), dto.getOriginalPrice());
        assertEquals(new BigDecimal("22.50"), dto.getFinalPrice());
        assertEquals(50, dto.getStock());
        assertEquals("AVAILABLE", dto.getStatus());
        assertEquals("/cover.jpg", dto.getCoverImage());
        assertEquals("/preview.pdf", dto.getPreviewPdf());
        assertTrue(dto.isFeatured());
        assertEquals("seller", dto.getSellerName());
        assertEquals(200L, dto.getSellerId());
        assertEquals(300L, dto.getCategoryId());
        assertEquals("Fiction", dto.getCategoryName());
        assertEquals("DISCOUNT", dto.getPricingStrategyUsed());
    }
}