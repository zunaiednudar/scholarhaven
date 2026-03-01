package com.example.scholarhaven.controller;

import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("featuredBooks", bookService.getFeaturedBooks());
        model.addAttribute("recentBooks", bookService.getRecentBooks(8));
        model.addAttribute("newArrivals", bookService.getRecentBooks(4));
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}