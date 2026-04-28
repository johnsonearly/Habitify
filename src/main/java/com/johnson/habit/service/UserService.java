package com.johnson.habit.service;

import com.johnson.habit.dto.UserDTO;

public interface UserService {
    UserDTO fetchUserProfileByUserName(String username);
    void createUser(UserDTO userDTO);


}
