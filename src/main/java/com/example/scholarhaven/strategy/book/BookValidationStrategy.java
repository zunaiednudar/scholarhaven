package com.example.scholarhaven.strategy.book;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.User;

public interface BookValidationStrategy {
    void validate(Book book, User user);
    boolean canPerformAction(User user);
}