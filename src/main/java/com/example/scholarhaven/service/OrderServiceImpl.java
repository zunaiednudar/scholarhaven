package com.example.scholarhaven.service;

import com.example.scholarhaven.dto.*;
import com.example.scholarhaven.entity.*;
import com.example.scholarhaven.repository.BookRepository;
import com.example.scholarhaven.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request, User buyer) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        Order order = Order.builder()
                .buyer(buyer)
                .status(Order.OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new RuntimeException(
                            "Book not found: " + itemRequest.getBookId()));

            // Check book is available
            if (book.getStatus() != Book.BookStatus.AVAILABLE) {
                throw new RuntimeException("Book is not available: " + book.getTitle());
            }

            // Check stock
            if (book.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for book: " + book.getTitle());
            }

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .book(book)
                    .quantity(itemRequest.getQuantity())
                    .priceAtPurchase(book.getPrice())
                    .build();

            order.getItems().add(item);

            // Deduct stock
            book.setStock(book.getStock() - itemRequest.getQuantity());
            bookRepository.save(book);

            total = total.add(
                    book.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        return OrderResponseDTO.fromEntity(findOrderById(id));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByBuyer(User buyer) {
        return orderRepository.findByBuyerOrderByCreatedAtDesc(buyer)
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, Order.OrderStatus newStatus, User admin) {
        if (!admin.hasRole("ADMIN")) {
            throw new RuntimeException("Only admins can update order status");
        }

        Order order = findOrderById(id);
        order.setStatus(newStatus);
        return OrderResponseDTO.fromEntity(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void cancelOrder(Long id, User buyer) {
        Order order = findOrderById(id);

        boolean isOwner = order.getBuyer().getId().equals(buyer.getId());
        boolean isAdmin = buyer.hasRole("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You don't have permission to cancel this order");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setStock(book.getStock() + item.getQuantity());
            bookRepository.save(book);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
}