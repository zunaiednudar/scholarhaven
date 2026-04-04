package com.example.scholarhaven.dto;

import com.example.scholarhaven.entity.OrderItem;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverImage;  // ← ADD THIS FIELD
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;

    public static OrderItemResponseDTO fromEntity(OrderItem item) {
        if (item == null) return null;
        
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .bookId(item.getBook() != null ? item.getBook().getId() : null)
                .bookTitle(item.getBook() != null ? item.getBook().getTitle() : null)
                .bookAuthor(item.getBook() != null ? item.getBook().getAuthor() : null)
                .bookCoverImage(item.getBook() != null ? item.getBook().getCoverImage() : null)  // ← ADD THIS
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(item.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}