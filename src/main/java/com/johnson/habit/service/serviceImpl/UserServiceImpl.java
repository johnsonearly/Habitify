package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.dto.UserDTO;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.repository.UserRepository;
import com.johnson.habit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO fetchUserProfileByUserName(String username) {
        try {
            UserEntity userEntity = userRepository.findByUsername(username);

            if (userEntity == null) {
                return null;
            }

            log.info("Fetching user profile from database for username {}", username);
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(userEntity.getUsername());
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setAvatarUrl(userEntity.getAvatarUrl());
            userDTO.setPassword(userEntity.getPassword());

            return userDTO;

        } catch (Exception e) {
            log.error("Error while fetching user profile from database", e);
            return null;
        }
    }
    public void createUser(UserDTO userDTO) {
        try
        {
            log.info("Creating user profile for username {}", userDTO.getUsername());
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(userDTO.getUsername());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setAvatarUrl(userDTO.getAvatarUrl());
            userRepository.save(userEntity);
        }  catch (Exception e){
            log.error("Error while creating user profile for username {}", userDTO.getUsername());
            log.error(e.getMessage());
        }
    }



}
