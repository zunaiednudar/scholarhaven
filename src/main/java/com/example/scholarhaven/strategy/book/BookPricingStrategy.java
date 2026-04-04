package com.example.scholarhaven.strategy.book;

import com.example.scholarhaven.entity.Book;
import java.math.BigDecimal;

public interface BookPricingStrategy {
    BigDecimal calculatePrice(Book book);
    String getStrategyName();
}