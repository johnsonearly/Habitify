package com.johnson.habit.auth.config;

import com.johnson.habit.auth.util.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                // 1. Disable CSRF for stateless APIs
                .csrf(csrf -> csrf.disable())

                // 2. Set session management to STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Explicitly override the authentication entry point to stop the "Pre-authenticated" rejection
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // If anything fails authentication, return 401 Unauthorized instead of a silent 403
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                // 4. Configure robust URL matching
                .authorizeHttpRequests(auth -> auth
                        // Handle internal dispatches (like /error) so they don't loop into a 403
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD, jakarta.servlet.DispatcherType.ERROR).permitAll()
                        // Open up your authentication endpoints completely
                        .requestMatchers("/auth/**", "/api/auth/**").permitAll()
                        // Everything else requires the filter context to be set
                        .anyRequest().authenticated()
                )

                // 5. Inject your custom JWT filter before the standard authentication gate
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}