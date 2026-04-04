package com.example.scholarhaven.controller;

import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("rolesList", user.getRoles().stream().map(r -> r.getName()).sorted().reduce((a, b) -> a + ", " + b).orElse("BUYER"));
        model.addAttribute("bookCount", user.getBooks() != null ? user.getBooks().size() : 0);
        return "profile";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        userService.deleteUserByUsername(userDetails.getUsername());

        // After deletion, force logout and redirect to homepage or login.
        return "redirect:/login?accountDeleted";
    }
}
