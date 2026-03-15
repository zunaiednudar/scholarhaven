package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.BookResponseDTO;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.service.BookService;
import com.example.scholarhaven.service.CategoryService;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/books")
    public String listBooks(@RequestParam(required = false) String q,
                            @RequestParam(required = false) Long categoryId,
                            Model model) {
        List<BookResponseDTO> books;

        if (categoryId != null) {
            var category = categoryService.getCategoryById(categoryId);
            if (q != null && !q.isEmpty()) {
                books = bookService.searchBooksByCategory(categoryId, q);
                model.addAttribute("searchQuery", q);
                model.addAttribute("title", "Search in " + category.getName() + " for \"" + q + "\"");
            } else {
                books = bookService.getBooksByCategory(categoryId);
                model.addAttribute("title", category.getName() + " Books");
            }
            model.addAttribute("currentCategory", categoryId);
        } else if (q != null && !q.isEmpty()) {
            books = bookService.searchBooks(q);
            model.addAttribute("searchQuery", q);
            model.addAttribute("title", "Search Results for \"" + q + "\"");
        } else {
            books = bookService.getAllAvailableBooks();
            model.addAttribute("title", "All Books");
        }

        model.addAttribute("books", books);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books";
    }

    @GetMapping("/my-books-dashboard")
    public String myBooksDashboard() {
        return "my-books-dashboard";
    }

    @GetMapping("/books/category/{categoryId}")
    public String booksByCategory(@PathVariable Long categoryId,
                                  @RequestParam(required = false) String q,
                                  Model model) {
        return listBooks(q, categoryId, model);
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        BookResponseDTO book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book-detail";
    }

    @GetMapping("/books/featured")
    public String featuredBooks(Model model) {
        List<BookResponseDTO> books = bookService.getFeaturedBooks();
        model.addAttribute("books", books);
        model.addAttribute("title", "Featured Books");
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books";
    }

    @GetMapping("/books/new-arrivals")
    public String newArrivals(Model model) {
        List<BookResponseDTO> books = bookService.getRecentBooks(20);
        model.addAttribute("books", books);
        model.addAttribute("title", "New Arrivals");
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books";
    }

    @GetMapping("/add-book")
    public String showAddBookForm(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pricingStrategies", bookService.getAvailablePricingStrategies());
        return "add-book";
    }

    @GetMapping("/my-books")
    public String myBooks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            User seller = userService.findByUsername(userDetails.getUsername());
            List<BookResponseDTO> books = bookService.getBooksBySeller(seller);
            model.addAttribute("books", books);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "my-books";
        } catch (Exception e) {
            return "redirect:/login?error=database";
        }
    }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername());
            BookResponseDTO book = bookService.getBookById(id);

            boolean isAdmin = user.hasRole("ADMIN");
            boolean isOwner = book.getSellerId().equals(user.getId());

            if (!isAdmin && !isOwner) {
                return "redirect:/books?error=unauthorized";
            }

            model.addAttribute("book", book);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pricingStrategies", bookService.getAvailablePricingStrategies());
            return "edit-book";
        } catch (Exception e) {
            return "redirect:/books";
        }
    }
}