package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemRequestDTOUnitTest {

    @Test
    void testSettersAndGetters() {
        OrderItemRequestDTO dto = new OrderItemRequestDTO();

        dto.setBookId(100L);
        dto.setQuantity(3);

        assertEquals(100L, dto.getBookId());
        assertEquals(3, dto.getQuantity());
    }

    @Test
    void testDefaultValues() {
        OrderItemRequestDTO dto = new OrderItemRequestDTO();

        assertNull(dto.getBookId());
        assertNull(dto.getQuantity());
    }
}