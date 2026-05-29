package com.johnson.habit.auth.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Use getServletPath() to safely ignore any server context-paths (e.g., /api)
        String path = request.getServletPath();

        // 2. Safely skip filtering for your public auth endpoints
        if (path.contains("/auth") || path.startsWith("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the Authorization header
        String authHeader = request.getHeader("Authorization");

        // 4. If header is missing or malformed, pass it down the chain.
        // Spring Security's .anyRequest().authenticated() will block it natively if required.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 5. Extract and parse the token string
            String token = authHeader.substring(7);

            // Assuming your JwtUtil returns a Claims object or map
            var claims = JwtUtil.validateToken(token);
            String username = claims.get("username", String.class);

            // 6. If username exists and user isn't already authenticated in this request session
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Create the principal authentication token for Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,         // Principal (The user identity)
                        null,             // Credentials (JWT acts as credentials, so null is fine here)
                        new ArrayList<>() // Authorities/Roles (Pass user roles here if you implement RBAC later)
                );

                // Build request details (IP, session ID, etc.) into the authentication token
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // IMPORTANT: Elevate the user context to "Authenticated"
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Optional: Store the username as a request attribute for easy access in Controllers
                request.setAttribute("username", username);
            }

        } catch (Exception e) {
            // 7. CRITICAL DEBUG STEP: If validation fails (expired token, wrong key), log it explicitly!
            System.err.println("JWT Filter Error: Validation failed. Reason: " + e.getMessage());
            e.printStackTrace();

            // Clear any partial authentication state to guarantee safety
            SecurityContextHolder.clearContext();
        }

        // 8. Continue the filter execution chain
        filterChain.doFilter(request, response);
    }
}