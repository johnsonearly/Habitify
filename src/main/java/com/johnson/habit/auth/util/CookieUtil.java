package com.johnson.habit.auth.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)                // Stops JavaScript from reading the cookie (Crucial XSS defense)
                .secure(true)                  // Forces transmission only over HTTPS connections
                .path("/")                     // Makes the cookie accessible across your entire API route
                .maxAge(804000/1000)     // Cookie lifespan (e.g., 7 days in seconds)
                .sameSite("None")            // Guards against CSRF attacks
                // .domain("yourdomain.com")   // Uncomment and change for production cross-domain sharing
                .build();
    }
    public static ResponseCookie createCleanCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
    public static ResponseCookie createCleanCookie(String token, long durationInSeconds) {
        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(durationInSeconds)
                .sameSite("Strict")
                .build();
    }




}

