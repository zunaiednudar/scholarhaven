package com.example.scholarhaven.dto;

import com.example.scholarhaven.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
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
        if (order == null) return null;
        
        return OrderResponseDTO.builder()
                .id(order.getId())
                .buyerId(order.getBuyer() != null ? order.getBuyer().getId() : null)
                .buyerUsername(order.getBuyer() != null ? order.getBuyer().getUsername() : null)
                .buyerName(order.getBuyer() != null ? order.getBuyer().getName() : null)
                .items(order.getItems() != null ? 
                        order.getItems().stream()
                                .map(OrderItemResponseDTO::fromEntity)
                                .collect(Collectors.toList()) : 
                        List.of())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .createdAt(order.getCreatedAt())
                .build();
    }
}