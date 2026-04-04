package com.example.scholarhaven.strategy.book;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.User;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class BookStrategyContext {

    private final Map<String, BookValidationStrategy> validationStrategies = new HashMap<>();
    private final Map<String, BookPricingStrategy> pricingStrategies = new HashMap<>();
    private BookValidationStrategy currentValidationStrategy;
    private BookPricingStrategy currentPricingStrategy;

    public BookStrategyContext(
            DefaultBookValidationStrategy defaultStrategy,
            SellerBookValidationStrategy sellerStrategy,
            AdminBookValidationStrategy adminStrategy,
            StandardPricingStrategy standardPricing) {
        
        validationStrategies.put("DEFAULT", defaultStrategy);
        validationStrategies.put("SELLER", sellerStrategy);
        validationStrategies.put("ADMIN", adminStrategy);
        
        pricingStrategies.put("STANDARD", standardPricing);
        
        currentValidationStrategy = defaultStrategy;
        currentPricingStrategy = standardPricing;
    }

    public void setValidationStrategyForUser(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            currentValidationStrategy = validationStrategies.get("DEFAULT");
        } else {
            if (user.hasRole("ADMIN")) {
                currentValidationStrategy = validationStrategies.get("ADMIN");
            }
            else if (user.hasRole("SELLER")) {
                currentValidationStrategy = validationStrategies.get("SELLER");
            }
            else {
                currentValidationStrategy = validationStrategies.get("DEFAULT");
            }
        }
    }

    public void setPricingStrategy(String strategyName) {
        BookPricingStrategy strategy = pricingStrategies.get(strategyName);
        if (strategy != null) {
            currentPricingStrategy = strategy;
        }
    }

    public void validateBook(Book book, User user) {
        if (currentValidationStrategy == null) {
            throw new RuntimeException("No validation strategy set");
        }
        currentValidationStrategy.validate(book, user);
    }

    public BigDecimal calculatePrice(Book book) {
        if (currentPricingStrategy == null) {
            throw new RuntimeException("No pricing strategy set");
        }
        return currentPricingStrategy.calculatePrice(book);
    }

    public String getCurrentPricingStrategyName() {
        return currentPricingStrategy != null ?
                currentPricingStrategy.getStrategyName() : "Standard";
    }

    public Map<String, String> getAvailablePricingStrategies() {
        Map<String, String> strategies = new HashMap<>();
        pricingStrategies.forEach((key, strategy) ->
                strategies.put(key, strategy.getStrategyName())
        );
        return strategies;
    }
}