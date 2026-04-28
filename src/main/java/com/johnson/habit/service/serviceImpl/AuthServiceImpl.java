package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.auth.util.JwtUtil;
import com.johnson.habit.dto.LoginRequestDTO;
import com.johnson.habit.dto.UserDTO;
import com.johnson.habit.service.AuthService;
import com.johnson.habit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl  implements AuthService{
    private final UserService userService;
    private  final PasswordEncoder  passwordEncoder;


    @Override
    public ResponseEntity<?> login(LoginRequestDTO loginRequest ) {
        try {
            UserDTO user = userService.fetchUserProfileByUserName(loginRequest.getUsername());
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Invalid password");
            }
            String token = JwtUtil.generateToken(user.getUsername());
            return  ResponseEntity.ok(Map.of(
                    "status", 200,
                    "token", token
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> register( UserDTO user) {
        userService.createUser(user);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "User registered successfully"
        ));
    }
}
