package com.example.scholarhaven.strategy.book;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.User;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DefaultBookValidationStrategy implements BookValidationStrategy {

    @Override
    public void validate(Book book, User user) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Book title is required");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new RuntimeException("Book author is required");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price must be greater than zero");
        }
        if (book.getStock() == null || book.getStock() < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }
    }

    @Override
    public boolean canPerformAction(User user) {
        return true;
    }
}