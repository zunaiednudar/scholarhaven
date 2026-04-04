package com.example.scholarhaven.dto;

import com.example.scholarhaven.entity.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private Integer stock;
    private String status;
    private String coverImage;
    private String previewPdf;
    private boolean featured;
    private String sellerName;
    private Long sellerId;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private String pricingStrategyUsed;

    public static BookResponseDTO fromEntity(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setOriginalPrice(book.getPrice());
        dto.setStock(book.getStock());
        dto.setStatus(book.getStatus().toString());
        dto.setCoverImage(book.getCoverImage());
        dto.setFeatured(book.isFeatured());
        dto.setCreatedAt(book.getCreatedAt());

        if (book.getSeller() != null) {
            dto.setSellerName(book.getSeller().getUsername());
            dto.setSellerId(book.getSeller().getId());
        }

        if (book.getCategory() != null) {
            dto.setCategoryId(book.getCategory().getId());
            dto.setCategoryName(book.getCategory().getName());
        }

        dto.setPreviewPdf(book.getPreviewPdf());

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getPreviewPdf() {
        return previewPdf;
    }

    public void setPreviewPdf(String previewPdf) {
        this.previewPdf = previewPdf;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPricingStrategyUsed() {
        return pricingStrategyUsed;
    }

    public void setPricingStrategyUsed(String pricingStrategyUsed) {
        this.pricingStrategyUsed = pricingStrategyUsed;
    }
}