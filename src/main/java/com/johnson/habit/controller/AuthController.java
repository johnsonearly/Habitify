package com.johnson.habit.controller;

import com.johnson.habit.auth.util.JwtUtil;
import com.johnson.habit.dto.LoginRequestDTO;
import com.johnson.habit.dto.UserDTO;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.repository.UserRepository;
import com.johnson.habit.service.AuthService;
import com.johnson.habit.service.UserService;
import com.johnson.habit.service.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
//@CrossOrigin(
//        origins = "http://localhost:5173",
//        allowCredentials = "true"
//)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
            return  ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody UserDTO user) {

        return ResponseEntity.ok(authService.register(user));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.refreshAccessToken(httpServletRequest));

    }

}