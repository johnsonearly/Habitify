package com.johnson.habit.service;

import com.johnson.habit.dto.LoginRequestDTO;
import com.johnson.habit.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequestDTO loginRequest );
    ResponseEntity<?> register( UserDTO user);
    ResponseEntity<?> refreshAccessToken(HttpServletRequest request);
    ResponseEntity<?> logout();
}
