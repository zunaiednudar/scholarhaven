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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final UserService userService;

    @GetMapping("/become-seller")
    public String showBecomeSellerPage(@AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("alreadySeller", user.hasRole("SELLER"));

        return "become-seller";
    }

    @PostMapping("/register")
    public String registerAsSeller(@AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            userService.addRoleToUser(userDetails.getUsername(), "SELLER");
            redirectAttributes.addFlashAttribute("success",
                    "Congratulations! You are now a seller. You can start listing your books.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to register as seller: " + e.getMessage());
        }

        return "redirect:/seller/become-seller?success=true";
    }
}