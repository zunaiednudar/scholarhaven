package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestDTOUnitTest {

    @Test
    void testSettersAndGetters() {
        OrderRequestDTO dto = new OrderRequestDTO();

        OrderItemRequestDTO item1 = new OrderItemRequestDTO();
        item1.setBookId(1L);
        item1.setQuantity(2);

        OrderItemRequestDTO item2 = new OrderItemRequestDTO();
        item2.setBookId(2L);
        item2.setQuantity(1);

        List<OrderItemRequestDTO> items = Arrays.asList(item1, item2);
        dto.setItems(items);

        assertNotNull(dto.getItems());
        assertEquals(2, dto.getItems().size());
        assertEquals(1L, dto.getItems().get(0).getBookId());
        assertEquals(2, dto.getItems().get(0).getQuantity());
        assertEquals(2L, dto.getItems().get(1).getBookId());
        assertEquals(1, dto.getItems().get(1).getQuantity());
    }

    @Test
    void testDefaultValues() {
        OrderRequestDTO dto = new OrderRequestDTO();

        assertNull(dto.getItems());
    }

    @Test
    void testEmptyList() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setItems(List.of());

        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }
}