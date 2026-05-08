package com.johnson.habit.service;

import com.johnson.habit.dto.UserDTO;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.response.SuccessResponse;

public interface UserService {
    SuccessResponse<UserDTO> fetchUserProfileByUserName(String username);
    SuccessResponse<UserEntity> createUser(UserDTO userDTO);


}
