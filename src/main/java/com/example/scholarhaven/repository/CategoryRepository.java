package com.example.scholarhaven.repository;

import com.example.scholarhaven.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c ORDER BY c.name ASC")
    List<Category> findAllOrderByNameAsc();

    @Query("SELECT c FROM Category c WHERE SIZE(c.books) > 0")
    List<Category> findCategoriesWithBooks();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.category.id = :categoryId")
    long countBooksById(@Param("categoryId") Long categoryId);
}