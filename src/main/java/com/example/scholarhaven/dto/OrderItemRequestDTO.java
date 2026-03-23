package com.example.scholarhaven.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemRequestDTO {
    private Long bookId;
    private Integer quantity;
}