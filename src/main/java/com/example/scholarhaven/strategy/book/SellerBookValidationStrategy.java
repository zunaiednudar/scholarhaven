package com.example.scholarhaven.strategy.book;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellerBookValidationStrategy implements BookValidationStrategy {

    private final DefaultBookValidationStrategy defaultStrategy;

    @Override
    public void validate(Book book, User user) {
        defaultStrategy.validate(book, user);

        if (book.getSeller() == null) {
            throw new RuntimeException("Seller must be assigned");
        }
        if (!book.getSeller().getId().equals(user.getId())) {
            throw new RuntimeException("You can only manage your own books");
        }
        if (book.getPrice().doubleValue() < 1.0) {
            throw new RuntimeException("Minimum price is $1.00");
        }
    }

    @Override
    public boolean canPerformAction(User user) {
        // ✅ FIXED: Use hasRole() to check for SELLER or ADMIN
        return user != null && (user.hasRole("SELLER") || user.hasRole("ADMIN"));
    }
}