package com.example.scholarhaven.repository;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBySeller(User seller);
    List<Book> findByStatus(Book.BookStatus status);
    List<Book> findByFeaturedTrue();
    List<Book> findByCategory(Category category);
    List<Book> findByCategoryAndStatus(Category category, Book.BookStatus status);

    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' ORDER BY b.createdAt DESC")
    List<Book> findRecentBooks(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    List<Book> searchBooks(@Param("query") String query);

    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND b.category.id = :categoryId AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    List<Book> searchBooksByCategory(@Param("categoryId") Long categoryId, @Param("query") String query);

    @Query("SELECT b FROM Book b WHERE b.status = 'PENDING_APPROVAL'")
    List<Book> findPendingApprovalBooks();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.category = :category AND b.status = 'AVAILABLE'")
    long countAvailableBooksByCategory(@Param("category") Category category);

    boolean existsByIdAndSeller(Long id, User seller);
}