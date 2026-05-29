package com.johnson.habit.service;


import com.johnson.habit.response.SuccessResponse;

public interface AIGroupingService {
    SuccessResponse processNewUser(String username, String goalDescription);
}
