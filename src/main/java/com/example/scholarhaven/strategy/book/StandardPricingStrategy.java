package com.example.scholarhaven.strategy.book;

import com.example.scholarhaven.entity.Book;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class StandardPricingStrategy implements BookPricingStrategy {

    @Override
    public BigDecimal calculatePrice(Book book) {
        return book.getPrice();
    }

    @Override
    public String getStrategyName() {
        return "Standard Price";
    }
}