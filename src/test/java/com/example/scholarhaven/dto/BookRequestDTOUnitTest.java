package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BookRequestDTOUnitTest {

    @Test
    void testSettersAndGetters() {
        BookRequestDTO dto = new BookRequestDTO();

        dto.setTitle("Test Book");
        dto.setAuthor("Test Author");
        dto.setDescription("Test Description");
        dto.setPrice(new BigDecimal("29.99"));
        dto.setStock(10);
        dto.setCoverImage("/cover.jpg");
        dto.setPreviewPdf("/preview.pdf");
        dto.setFeatured(true);
        dto.setCategoryId(5L);
        dto.setPricingStrategy("DISCOUNT");

        assertEquals("Test Book", dto.getTitle());
        assertEquals("Test Author", dto.getAuthor());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(new BigDecimal("29.99"), dto.getPrice());
        assertEquals(10, dto.getStock());
        assertEquals("/cover.jpg", dto.getCoverImage());
        assertEquals("/preview.pdf", dto.getPreviewPdf());
        assertTrue(dto.isFeatured());
        assertEquals(5L, dto.getCategoryId());
        assertEquals("DISCOUNT", dto.getPricingStrategy());
    }

    @Test
    void testDefaultValues() {
        BookRequestDTO dto = new BookRequestDTO();

        assertNull(dto.getTitle());
        assertNull(dto.getAuthor());
        assertNull(dto.getDescription());
        assertNull(dto.getPrice());
        assertNull(dto.getStock());
        assertNull(dto.getCoverImage());
        assertNull(dto.getPreviewPdf());
        assertFalse(dto.isFeatured());
        assertNull(dto.getCategoryId());
        assertNull(dto.getPricingStrategy());
    }

    @Test
    void testSetFeatured() {
        BookRequestDTO dto = new BookRequestDTO();
        assertFalse(dto.isFeatured());

        dto.setFeatured(true);
        assertTrue(dto.isFeatured());

        dto.setFeatured(false);
        assertFalse(dto.isFeatured());
    }
}