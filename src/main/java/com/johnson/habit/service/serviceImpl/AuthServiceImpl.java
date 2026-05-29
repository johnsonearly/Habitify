package com.johnson.habit.service.serviceImpl;
import com.johnson.habit.dto.LoginRequestDTO;
import com.johnson.habit.dto.UserDTO;
import com.johnson.habit.response.exception.DefaultErrorException;
import com.johnson.habit.service.AuthService;
import com.johnson.habit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.johnson.habit.auth.util.CookieUtil.createCleanCookie;
import static com.johnson.habit.auth.util.CookieUtil.createRefreshTokenCookie;
import static com.johnson.habit.auth.util.JwtUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> login(LoginRequestDTO loginRequest) {
        try {
            log.info("[AuthService] Processing login request for user: {}", loginRequest.getUsername());
            UserDTO user = userService.fetchUserProfileByUserName(loginRequest.getUsername()).getData();

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
            }

            String accessToken = generateToken(user.getUsername());
            String refreshToken = generateRefreshToken(user.getUsername());

            ResponseCookie cookie = createRefreshTokenCookie(refreshToken);

            // Clean sensitive data before returning user reference context
            user.setPassword(null);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of(
                            "status", 200,
                            "accessToken", accessToken

                    ));
        } catch (Exception e) {
            log.error("[AuthService] Critical exception encountered during user login", e);
            throw new DefaultErrorException(e.getLocalizedMessage());
        }
    }

    @Override
    public ResponseEntity<?> register(UserDTO user) {
        try {
            log.info("[AuthService] Registering brand new user: {}", user.getUsername());
            userService.createUser(user);

            String accessToken = generateToken(user.getUsername());
            String refreshToken = generateRefreshToken(user.getUsername());

            ResponseCookie cookie = createRefreshTokenCookie(refreshToken);

            user.setPassword(null);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of(
                            "status", 200,
                            "message", "User registered successfully",
                            "accessToken", accessToken
                    ));
        } catch (Exception e) {
            log.error("[AuthService] Critical exception encountered during registration sequence", e);
            throw new DefaultErrorException(e.getLocalizedMessage());
        }
    }

    @Override
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        String refreshToken = null;

        // 1. Extract the cookie named 'refresh_token' out of the request stream
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.warn("[AuthService] Token refresh rejected: Secure cookie missing or null.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token is missing."));
        }

        try {
            // 2. CRITICAL FIX: Run validation first.
            // If expired or altered, this line instantly self-destructs into the catch block.
            io.jsonwebtoken.Claims claims = validateToken(refreshToken);

            // 3. FIX: Safely extract the subject (username) from the validated claims payload
            String username = claims.getSubject();

            // 4. FIX: Use the static utility reference to mint the fresh access token
            String newAccessToken = generateToken(username);
            log.info("[AuthService] Seamlessly rotated access token for username identity: {}", username);

            return ResponseEntity.ok().body(Map.of(
                    "accessToken", newAccessToken
            ));
        } catch (Exception e) {
            log.error("[AuthService] Silent verification failure during token rotation loop", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Session token has expired or is invalid. Log in again."));
        }
    }

    @Override
    public ResponseEntity<?> logout() {
        log.info("[AuthService] Clearing active session parameters via token blackholing");
        // Clear cookie space cleanly by generating an expired cookie (maxAge = 0) via cookieUtil
        ResponseCookie cleanCookie = createCleanCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(Map.of("message", "Logged out successfully."));
    }
}