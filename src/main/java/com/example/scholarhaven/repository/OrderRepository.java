package com.example.scholarhaven.repository;

import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get orders by buyer, sorted by creation date descending
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);

    // Get orders by status
    List<Order> findByStatus(Order.OrderStatus status);

    // Get all orders sorted by creation date descending
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllOrderByCreatedAtDesc();

    // Count orders by status
    long countByStatus(Order.OrderStatus status);
    
    // Delete all orders by buyer (useful for user deletion)
    @Transactional
    @Modifying
    void deleteByBuyer(User buyer);
    
    // Get orders count by buyer
    long countByBuyer(User buyer);
    
    // Get orders by buyer and status
    List<Order> findByBuyerAndStatus(User buyer, Order.OrderStatus status);
}