package com.example.scholarhaven.dto;

import com.example.scholarhaven.entity.Order;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private Long buyerId;
    private String buyerUsername;
    private String buyerName;
    private List<OrderItemResponseDTO> items;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;

    public static OrderResponseDTO fromEntity(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .buyerId(order.getBuyer().getId())
                .buyerUsername(order.getBuyer().getUsername())
                .buyerName(order.getBuyer().getName())
                .items(order.getItems().stream()
                        .map(OrderItemResponseDTO::fromEntity)
                        .collect(Collectors.toList()))
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }
}