package com.example.scholarhaven.service;

import com.example.scholarhaven.dto.OrderRequestDTO;
import com.example.scholarhaven.dto.OrderResponseDTO;
import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.entity.User;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request, User buyer);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByBuyer(User buyer);
    List<OrderResponseDTO> getAllOrders();
    List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status);
    OrderResponseDTO updateOrderStatus(Long id, Order.OrderStatus newStatus, User admin);
    void cancelOrder(Long id, User buyer);
    long countByStatus(Order.OrderStatus status);
}