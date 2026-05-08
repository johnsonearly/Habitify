package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.dto.UserDTO;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.repository.UserRepository;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.response.exception.DefaultErrorException;
import com.johnson.habit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    

    public SuccessResponse<UserDTO> fetchUserProfileByUserName(String username) {
        log.info("Inside fetchUserProfileByUserName - start");
        try {
            UserEntity userEntity = userRepository.findByUsername(username);

            if (userEntity == null) {
                return null;
            }

            log.info("Fetching user profile from database for username {}", username);
            return  getResponse(userEntity);
        } catch (Exception e) {
            log.error("Error while fetching user profile from database", e);
            throw new DefaultErrorException(e.getMessage());
        }
    }

    private static @NonNull SuccessResponse<UserDTO> getResponse(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setAvatarUrl(userEntity.getAvatarUrl());
        userDTO.setPassword(userEntity.getPassword());

        // Construct success response
        SuccessResponse<UserDTO> successResponse = new SuccessResponse<>();
        successResponse.setData(userDTO);
        successResponse.setMessage("Successfully fetched user profile");
        successResponse.setStatusCode(200);
        return successResponse;
    }

    public SuccessResponse<UserEntity> createUser(UserDTO userDTO) {
        try
        {
            log.info("Creating user profile for username {}", userDTO.getUsername());
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(userDTO.getUsername());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setAvatarUrl(userDTO.getAvatarUrl());

            SuccessResponse<UserEntity> successResponse = new SuccessResponse<>();
            successResponse.setData(userEntity);
            successResponse.setMessage("Successfully created user profile");
            successResponse.setStatusCode(201);
            userRepository.save(userEntity);

            return successResponse;

        }  catch (Exception e){
            log.error("Error while creating user profile for username {}", userDTO.getUsername());
            log.error(e.getMessage());
            throw new DefaultErrorException(e.getMessage());
        }

    }



}
