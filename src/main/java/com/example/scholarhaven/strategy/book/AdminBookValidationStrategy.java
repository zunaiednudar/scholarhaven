```java
package com.example.scholarhaven.strategy.book;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBookValidationStrategy implements BookValidationStrategy {

    private final DefaultBookValidationStrategy defaultStrategy;

    @Override
    public void validate(Book book, User user) {
        defaultStrategy.validate(book, user);
        if (book.getSeller() == null) {
            throw new RuntimeException("Book must have a seller");
        }
    }

    @Override
    public boolean canPerformAction(User user) {
        return user != null && user.hasRole("ADMIN");
    }
}