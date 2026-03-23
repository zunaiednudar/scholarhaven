package com.example.scholarhaven.repository;

import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders by a specific buyer, newest first
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);

    // Get all orders by status
    List<Order> findByStatus(Order.OrderStatus status);

    // Get all orders, newest first
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllOrderByCreatedAtDesc();

    // Count orders by status — used for admin dashboard stats
    long countByStatus(Order.OrderStatus status);
}