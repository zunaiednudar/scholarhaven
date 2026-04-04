package com.example.scholarhaven.controller;

import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    @Transactional(readOnly = true)
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
    @Transactional
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails, 
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to delete your account");
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        
        try {
            
            User user = userService.findByUsername(username);
            if (user.hasRole("ADMIN")) {
                redirectAttributes.addFlashAttribute("error", "Admin accounts cannot be deleted");
                return "redirect:/profile?error=admin";
            }
            
            userService.deleteUserByUsername(username);
            redirectAttributes.addFlashAttribute("success", "Your account has been deleted successfully.");
            return "redirect:/logout";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete account: " + e.getMessage());
            return "redirect:/profile?error=delete";
        }
    }


    
    @PostMapping("/api/profile/delete")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> deleteAccountApi(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        
        if (userDetails == null) {
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        String username = userDetails.getUsername();
        
        try {
            User user = userService.findByUsername(username);
            
     
            if (user.hasRole("ADMIN")) {
                response.put("error", "Admin accounts cannot be deleted");
                return ResponseEntity.status(403).body(response);
            }
            
            userService.deleteUserByUsername(username);
            
            response.put("success", true);
            response.put("message", "Account deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
