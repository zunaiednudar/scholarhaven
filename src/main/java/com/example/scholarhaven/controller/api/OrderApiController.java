package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.dto.OrderRequestDTO;
import com.example.scholarhaven.dto.OrderResponseDTO;
import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.OrderService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;
    private final UserService userService;

    // BUYER ENDPOINTS

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody OrderRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        System.out.println("CREATE ORDER");
        System.out.println("UserDetails: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        if (userDetails == null) {
            System.out.println("User not authenticated");
            return ResponseEntity.status(401).build();
        }
        
        try {
            User buyer = userService.findByUsername(userDetails.getUsername());
            System.out.println("Buyer: " + buyer.getUsername() + " (ID: " + buyer.getId() + ")");
            OrderResponseDTO order = orderService.createOrder(request, buyer);
            System.out.println("Order created: ID=" + order.getId());
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        User buyer = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getOrdersByBuyer(buyer));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        OrderResponseDTO order = orderService.getOrderById(id);

        boolean isOwner = order.getBuyerId().equals(user.getId());
        boolean isAdmin = user.hasRole("ADMIN");

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User buyer = userService.findByUsername(userDetails.getUsername());
        orderService.cancelOrder(id, buyer);
        return ResponseEntity.ok().build();
    }

    // ADMIN ENDPOINTS

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, admin));
    }
}