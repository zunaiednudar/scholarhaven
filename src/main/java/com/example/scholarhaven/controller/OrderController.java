package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.OrderResponseDTO;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.OrderService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        System.out.println("MY ORDERS PAGE ACCESSED");
        
        if (userDetails == null) {
            System.out.println("User not authenticated - redirecting to login");
            return "redirect:/login";
        }
        
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            System.out.println("User found: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            List<OrderResponseDTO> orders = orderService.getOrdersByBuyer(user);
            System.out.println("Found " + orders.size() + " orders for user: " + user.getUsername());
            
            // Debug - print each order
            for (OrderResponseDTO order : orders) {
                System.out.println("   Order ID: " + order.getId() + 
                                   ", Status: " + order.getStatus() + 
                                   ", Total: $" + order.getTotalPrice() +
                                   ", Items: " + (order.getItems() != null ? order.getItems().size() : 0));
            }
            
            model.addAttribute("orders", orders);
            
            System.out.println("Orders page loaded successfully");
            System.out.println("\n");
            return "orders";
            
        } catch (Exception e) {
            System.out.println("Error loading orders: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("orders", List.of());
            model.addAttribute("error", "Could not load orders: " + e.getMessage());
            return "orders";
        }
    }

    @GetMapping("/orders/{id}")
    @Transactional(readOnly = true)
    public String orderDetail(@PathVariable Long id, 
                              @AuthenticationPrincipal UserDetails userDetails, 
                              Model model) {
        System.out.println("ORDER DETAIL PAGE ACCESSED");
        System.out.println("Order ID requested: " + id);
        
        if (userDetails == null) {
            System.out.println("User not authenticated - redirecting to login");
            return "redirect:/login";
        }
        
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            System.out.println("User found: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            OrderResponseDTO order = orderService.getOrderById(id);
            System.out.println(" Order found: ID=" + order.getId() +
                               ", Status=" + order.getStatus() + 
                               ", Total=$" + order.getTotalPrice() +
                               ", Items=" + (order.getItems() != null ? order.getItems().size() : 0));
            
            // Check if user owns this order or is admin
            boolean isOwner = order.getBuyerId().equals(user.getId());
            boolean isAdmin = user.hasRole("ADMIN");
            
            System.out.println("Is owner: " + isOwner);
            System.out.println("Is admin: " + isAdmin);
            
            if (!isOwner && !isAdmin) {
                System.out.println("User not authorized to view order: " + id);
                return "redirect:/orders?error=unauthorized";
            }
            
            model.addAttribute("order", order);
            System.out.println("Order detail loaded successfully");
            System.out.println("\n");
            return "order-detail";
            
        } catch (Exception e) {
            System.out.println("Error loading order detail: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/orders?error=notfound&id=" + id;
        }
    }
}