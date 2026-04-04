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
        System.out.println("CREATING NEW ORDER");
        System.out.println("Buyer: " + buyer.getUsername() + " (ID: " + buyer.getId() + ")");
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        // Create order with PENDING status (needs admin approval)
        Order order = Order.builder()
                .buyer(buyer)
                .status(Order.OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found: " + itemRequest.getBookId()));

            if (book.getStatus() != Book.BookStatus.AVAILABLE) {
                throw new RuntimeException("Book is not available: " + book.getTitle());
            }

            if (book.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
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

            total = total.add(book.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);
        
        System.out.println("Order created with PENDING status! Order ID: " + savedOrder.getId());
        System.out.println("   Total: $" + total);
        System.out.println("\n");
        
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = findOrderById(id);
        return OrderResponseDTO.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByBuyer(User buyer) {
        List<Order> orders = orderRepository.findByBuyerOrderByCreatedAtDesc(buyer);
        
        for (Order order : orders) {
            order.getItems().size();
        }
        
        return orders.stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllOrderByCreatedAtDesc();
        
        for (Order order : orders) {
            order.getItems().size();
        }
        
        return orders.stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        
        for (Order order : orders) {
            order.getItems().size();
        }
        
        return orders.stream()
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
        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
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

        // Only allow cancellation if PENDING or CONFIRMED
        if (order.getStatus() != Order.OrderStatus.PENDING && 
            order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
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
    @Transactional(readOnly = true)
    public long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    // ADMIN APPROVAL METHODS

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getPendingApprovalOrders() {
        System.out.println("GETTING PENDING APPROVAL ORDERS");
        
        // Get orders with PENDING status (treat PENDING as "pending approval")
        List<Order> orders = orderRepository.findByStatus(Order.OrderStatus.PENDING);
        System.out.println("Found " + orders.size() + " orders pending approval");
        
        for (Order order : orders) {
            order.getItems().size();
        }
        
        return orders.stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO approveOrder(Long id, User admin) {
        System.out.println("APPROVING ORDER");
        System.out.println("Order ID: " + id);
        System.out.println("Admin: " + admin.getUsername());
        
        if (!admin.hasRole("ADMIN")) {
            throw new RuntimeException("Only admins can approve orders");
        }
        
        Order order = findOrderById(id);
        
        // Check if order is PENDING (waiting for approval)
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Order is not pending approval. Current status: " + order.getStatus());
        }
        
        // Approve: change PENDING to CONFIRMED
        order.setStatus(Order.OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);
        
        System.out.println("Order approved! Status: PENDING → CONFIRMED");
        System.out.println("\n");
        
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO rejectOrder(Long id, User admin) {
        System.out.println("REJECTING ORDER");
        System.out.println("Order ID: " + id);
        System.out.println("Admin: " + admin.getUsername());
        
        if (!admin.hasRole("ADMIN")) {
            throw new RuntimeException("Only admins can reject orders");
        }
        
        Order order = findOrderById(id);
        
        // Check if order is PENDING (waiting for approval)
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Order is not pending approval. Current status: " + order.getStatus());
        }
        
        // Restore stock for rejected orders
        System.out.println("Restoring stock for rejected order...");
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            int restoredStock = book.getStock() + item.getQuantity();
            book.setStock(restoredStock);
            bookRepository.save(book);
            System.out.println("  - Restored " + item.getQuantity() + " of '" + book.getTitle() + "' (stock: " + restoredStock + ")");
        }
        
        // Reject: change PENDING to CANCELLED
        order.setStatus(Order.OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        
        System.out.println("Order rejected! Status: PENDING → CANCELLED");
        System.out.println("\n");
        
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
}