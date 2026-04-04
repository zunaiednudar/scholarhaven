
package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.dto.OrderResponseDTO;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.OrderService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderApiController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getPendingApprovalOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== ADMIN GET PENDING ORDERS ==========");
        System.out.println("Admin: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        List<OrderResponseDTO> orders = orderService.getPendingApprovalOrders();
        System.out.println("Returning " + orders.size() + " pending approval orders");
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveOrder(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== ADMIN APPROVE ORDER ==========");
        System.out.println("Admin: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("Order ID to approve: " + id);
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            User admin = userService.findByUsername(userDetails.getUsername());
            OrderResponseDTO approvedOrder = orderService.approveOrder(id, admin);
            System.out.println("✅ Order approved successfully: ID " + id);
            return ResponseEntity.ok(approvedOrder);
        } catch (RuntimeException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectOrder(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== ADMIN REJECT ORDER ==========");
        System.out.println("Admin: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("Order ID to reject: " + id);
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            User admin = userService.findByUsername(userDetails.getUsername());
            OrderResponseDTO rejectedOrder = orderService.rejectOrder(id, admin);
            System.out.println("✅ Order rejected successfully: ID " + id);
            return ResponseEntity.ok(rejectedOrder);
        } catch (RuntimeException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
}
