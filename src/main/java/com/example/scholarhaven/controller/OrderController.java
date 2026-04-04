package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.OrderResponseDTO;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.OrderService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    // My orders page
    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";

        User buyer = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("orders", orderService.getOrdersByBuyer(buyer));
        return "orders";
    }

    // Single order detail page
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        if (userDetails == null) return "redirect:/login";

        User user = userService.findByUsername(userDetails.getUsername());
        OrderResponseDTO order = orderService.getOrderById(id);

        // Only buyer or admin can view
        boolean isOwner = order.getBuyerId().equals(user.getId());
        boolean isAdmin = user.hasRole("ADMIN");

        if (!isOwner && !isAdmin) {
            return "redirect:/orders?error=unauthorized";
        }

        model.addAttribute("order", order);
        return "order-detail";
    }

    // Checkout page
    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        return "checkout";
    }
}