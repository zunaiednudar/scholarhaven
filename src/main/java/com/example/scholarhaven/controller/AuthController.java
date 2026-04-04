package com.example.scholarhaven.controller;

import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.RoleRepository;
import com.example.scholarhaven.repository.UserRepository;
import com.example.scholarhaven.security.JwtService;
import com.example.scholarhaven.security.UserPrincipal;
import com.example.scholarhaven.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    // PAGE ROUTES (HTML)

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // FORM HANDLERS (POST)

    @PostMapping("/register")
    public String processRegister(@RequestParam String name,
                                  @RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }

        try {
            User user = new User();
            user.setName(name);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setEnabled(true);

            userService.registerNewUser(user);

            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        RedirectAttributes redirectAttributes) {
        try {
            userService.sendPasswordResetEmail(email);
            redirectAttributes.addFlashAttribute("success", "Password reset link sent to your email");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Email not found");
        }
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {

        try {
            // Ensure admin user exists for manual bypass credential
            if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
                userService.getOrCreateAdmin("admin", "admin123");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String jwt = jwtService.generateToken(userDetails);

            String cookieValue = "jwt=" + jwt + "; Path=/; Max-Age=86400; HttpOnly; SameSite=Lax";
            if ("https".equalsIgnoreCase(request.getScheme())) {
                cookieValue += "; Secure";
            }
            response.setHeader("Set-Cookie", cookieValue);

            if ("admin".equalsIgnoreCase(username)) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials or no permissions");
            return "redirect:/login";
        }
    }

    // API ENDPOINTS (JSON) for JWT

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            // Ensure a built-in admin account is always available for this exact credential pair
            if ("admin".equalsIgnoreCase(loginRequest.getUsername()) && "admin123".equals(loginRequest.getPassword())) {
                userService.getOrCreateAdmin("admin", "admin123");
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Load UserDetails
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // Generate JWT token using UserDetails
            String jwt = jwtService.generateToken(userDetails);

            // Set token as HttpOnly cookie to avoid JS SameSite/iframe issues
            String cookieValue = "jwt=" + jwt + "; Path=/; Max-Age=86400; HttpOnly; SameSite=Lax";
            if ("https".equalsIgnoreCase(request.getScheme())) {
                cookieValue += "; Secure";
            }
            response.setHeader("Set-Cookie", cookieValue);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity
                    .status(401)
                    .body(new ErrorResponse("Invalid username or password"));
        }
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> apiRegister(@RequestBody RegisterRequest registerRequest) {

        // Validate name is provided
        if (registerRequest.getName() == null || registerRequest.getName().trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Full name is required"));
        }

        // Validate passwords match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Passwords do not match"));
        }

        // Validate password length
        if (registerRequest.getPassword().length() < 6) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Password must be at least 6 characters"));
        }

        // Validate email format
        if (!registerRequest.getEmail().contains("@") || !registerRequest.getEmail().contains(".")) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Valid email is required"));
        }

        // Validate username
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Username is required"));
        }

        try {
            User user = new User();
            user.setName(registerRequest.getName());
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setEnabled(true);

            User registeredUser = userService.registerNewUser(user);

            return ResponseEntity.ok(new MessageResponse("Registration successful"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // USER ROLE MANAGEMENT API

    @GetMapping("/api/user/roles")
    @ResponseBody
    public ResponseEntity<?> getUserRoles(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("roles", user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
            response.put("userId", user.getId());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/user/info")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("enabled", user.getEnabled());
            response.put("roles", user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ROLE MANAGEMENT ENDPOINTS

    @PostMapping("/api/auth/add-seller-role")
    @ResponseBody
    public ResponseEntity<?> addSellerRole(@RequestBody Map<String, String> request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = request.get("username");

            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Username is required"));
            }

            // Verify the authenticated user is adding their own role or is admin
            if (!username.equals(userDetails.getUsername())) {
                User currentUser = userService.findByUsername(userDetails.getUsername());
                if (!currentUser.hasRole("ADMIN")) {
                    return ResponseEntity.status(403).body(new ErrorResponse("You can only add seller role to yourself"));
                }
            }

            userService.addRoleToUser(username, "SELLER");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Seller role added to user: " + username);
            response.put("success", "true");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // AUTH STATUS CHECK

    @GetMapping("/api/auth/check")
    @ResponseBody
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", isAuthenticated);

        if (isAuthenticated) {
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());

            if (auth.getPrincipal() instanceof UserPrincipal) {
                User user = ((UserPrincipal) auth.getPrincipal()).getUser();
                response.put("userId", user.getId());
                response.put("email", user.getEmail());
                response.put("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()));
            }
        }

        // Check cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Map<String, String> cookieMap = new HashMap<>();
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String val = cookie.getValue();
                    cookieMap.put(cookie.getName(), val.length() > 20 ? val.substring(0, 20) + "..." : val);
                } else {
                    cookieMap.put(cookie.getName(), cookie.getValue());
                }
            }
            response.put("cookies", cookieMap);
        }

        return ResponseEntity.ok(response);
    }

    // DEBUG ENDPOINTS

    @GetMapping("/debug/auth")
    @ResponseBody
    public String debugAuth(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: monospace; padding: 20px;'>");
        sb.append("<h2>🔐 Authentication Debug</h2>");
        sb.append("<p>Current time: ").append(java.time.LocalDateTime.now()).append("</p>");

        // Check cookies
        sb.append("<h3>🍪 Cookies:</h3><ul>");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                sb.append("<li><b>").append(cookie.getName()).append("</b>: ");
                if ("jwt".equals(cookie.getName())) {
                    String val = cookie.getValue();
                    sb.append(val.substring(0, Math.min(30, val.length()))).append("...");
                } else {
                    sb.append(cookie.getValue());
                }
                sb.append("</li>");
            }
        } else {
            sb.append("<li>No cookies found</li>");
        }
        sb.append("</ul>");

        // Check authentication
        sb.append("<h3>🔑 Security Context:</h3><pre>");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            sb.append("Is authenticated: ").append(auth.isAuthenticated()).append("\n");
            sb.append("Principal type: ").append(auth.getPrincipal().getClass().getName()).append("\n");
            sb.append("Name: ").append(auth.getName()).append("\n");
            sb.append("Authorities: ").append(auth.getAuthorities()).append("\n");

            if (auth.getPrincipal() instanceof UserPrincipal) {
                User user = ((UserPrincipal) auth.getPrincipal()).getUser();
                sb.append("\n👤 User Details:\n");
                sb.append("  ID: ").append(user.getId()).append("\n");
                sb.append("  Username: ").append(user.getUsername()).append("\n");
                sb.append("  Name: ").append(user.getName()).append("\n");
                sb.append("  Email: ").append(user.getEmail()).append("\n");
                sb.append("  Enabled: ").append(user.getEnabled()).append("\n");
                sb.append("  Roles: ");
                user.getRoles().forEach(role -> sb.append(role.getName()).append(" "));
                sb.append("\n");
            }
        } else {
            sb.append("No authentication in SecurityContext");
        }
        sb.append("</pre>");

        sb.append("</body></html>");
        return sb.toString();
    }

    @GetMapping("/debug/user-roles")
    @ResponseBody
    public String debugUserRoles(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "<html><body><h2>Not authenticated</h2><p>Please <a href='/login'>login</a> first.</p></body></html>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: monospace; padding: 20px;'>");
        sb.append("<h2>👤 User Roles Debug</h2>");
        sb.append("<p>Current time: ").append(java.time.LocalDateTime.now()).append("</p>");

        sb.append("<h3>UserDetails:</h3>");
        sb.append("<pre>");
        sb.append("Username: ").append(userDetails.getUsername()).append("\n");
        sb.append("Authorities: ").append(userDetails.getAuthorities()).append("\n");
        sb.append("Class: ").append(userDetails.getClass().getName()).append("\n");
        sb.append("</pre>");

        if (userDetails instanceof UserPrincipal) {
            User user = ((UserPrincipal) userDetails).getUser();
            sb.append("<h3>Database User:</h3>");
            sb.append("<pre>");
            sb.append("ID: ").append(user.getId()).append("\n");
            sb.append("Username: ").append(user.getUsername()).append("\n");
            sb.append("Name: ").append(user.getName()).append("\n");
            sb.append("Email: ").append(user.getEmail()).append("\n");
            sb.append("Enabled: ").append(user.getEnabled()).append("\n");
            sb.append("Roles: ");
            user.getRoles().forEach(role -> sb.append(role.getName()).append(" "));
            sb.append("\n");
            sb.append("Has SELLER role: ").append(user.hasRole("SELLER")).append("\n");
            sb.append("Has BUYER role: ").append(user.hasRole("BUYER")).append("\n");
            sb.append("Has ADMIN role: ").append(user.hasRole("ADMIN")).append("\n");
            sb.append("</pre>");
        }

        sb.append("<p><a href='/'>Back to Home</a> | <a href='/debug/auth'>Auth Debug</a></p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}

// DTO Classes

class LoginRequest {
    private String username;
    private String password;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class RegisterRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}

class JwtResponse {
    private String token;
    private String type = "Bearer";
    public JwtResponse(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

class ErrorResponse {
    private String message;
    public ErrorResponse(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

class MessageResponse {
    private String message;
    public MessageResponse(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}