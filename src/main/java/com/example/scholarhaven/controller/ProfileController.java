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
        System.out.println("========== PROFILE PAGE ACCESSED ==========");
        
        if (userDetails == null) {
            System.out.println("❌ UserDetails is NULL - Not authenticated");
            return "redirect:/login";
        }
        
        String username = userDetails.getUsername();
        System.out.println("✅ UserDetails username: " + username);
        
        try {
            User user = userService.findByUsername(username);
            
            int bookCount = user.getBooks() != null ? user.getBooks().size() : 0;
            
            System.out.println("✅ User found in database: " + user.getUsername());
            System.out.println("   Name: " + user.getName());
            System.out.println("   Email: " + user.getEmail());
            System.out.println("   Enabled: " + user.getEnabled());
            System.out.println("   Roles: " + user.getRoles().stream().map(r -> r.getName()).toList());
            System.out.println("   Books count: " + bookCount);
            
            model.addAttribute("user", user);
            model.addAttribute("rolesList", user.getRoles().stream()
                    .map(r -> r.getName())
                    .sorted()
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("BUYER"));
            model.addAttribute("bookCount", bookCount);
            
            System.out.println("✅ Profile page loaded successfully");
            System.out.println("========================================\n");
            return "profile";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR loading profile: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================\n");
            return "redirect:/login?error=profile";
        }
    }

    @PostMapping("/profile/delete")
    @Transactional
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails, 
                                RedirectAttributes redirectAttributes) {
        System.out.println("========== DELETE ACCOUNT (FORM) ==========");
        
        if (userDetails == null) {
            System.out.println("❌ User not authenticated");
            redirectAttributes.addFlashAttribute("error", "You must be logged in to delete your account");
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        System.out.println("Deleting account for user: " + username);
        
        try {
            // Check if trying to delete admin account
            User user = userService.findByUsername(username);
            if (user.hasRole("ADMIN")) {
                System.out.println("❌ Cannot delete admin account");
                redirectAttributes.addFlashAttribute("error", "Admin accounts cannot be deleted");
                return "redirect:/profile?error=admin";
            }
            
            userService.deleteUserByUsername(username);
            System.out.println("✅ Account deleted successfully: " + username);
            System.out.println("========================================\n");
            
            redirectAttributes.addFlashAttribute("success", "Your account has been deleted successfully.");
            return "redirect:/logout";
            
        } catch (Exception e) {
            System.out.println("❌ Error deleting account: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete account: " + e.getMessage());
            return "redirect:/profile?error=delete";
        }
    }

    // ========== API ENDPOINT FOR AJAX DELETE ==========
    
    @PostMapping("/api/profile/delete")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> deleteAccountApi(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== DELETE ACCOUNT API ==========");
        
        Map<String, Object> response = new HashMap<>();
        
        if (userDetails == null) {
            System.out.println("❌ User not authenticated");
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        String username = userDetails.getUsername();
        System.out.println("User from UserDetails: " + username);
        
        try {
            User user = userService.findByUsername(username);
            System.out.println("User found in DB: " + user.getUsername());
            System.out.println("User roles: " + user.getRoles().stream().map(r -> r.getName()).toList());
            
            // Check if trying to delete admin account
            if (user.hasRole("ADMIN")) {
                System.out.println("❌ Cannot delete admin account");
                response.put("error", "Admin accounts cannot be deleted");
                return ResponseEntity.status(403).body(response);
            }
            
            userService.deleteUserByUsername(username);
            System.out.println("✅ Account deleted successfully: " + username);
            System.out.println("========================================\n");
            
            response.put("success", true);
            response.put("message", "Account deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("❌ Error deleting account: " + e.getMessage());
            e.printStackTrace();
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}