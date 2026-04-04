package com.example.scholarhaven.controller;

import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pendingBooks", bookService.getPendingApprovalBooks());
        model.addAttribute("allBooks", bookService.getAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "admin/dashboard";
    }
}
