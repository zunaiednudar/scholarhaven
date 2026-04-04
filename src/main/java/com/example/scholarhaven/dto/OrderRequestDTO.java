package com.example.scholarhaven.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class OrderRequestDTO {
    private List<OrderItemRequestDTO> items;
}