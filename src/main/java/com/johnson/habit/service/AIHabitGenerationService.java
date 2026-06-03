package com.johnson.habit.service;

import com.johnson.habit.dto.GoalProfileDTO;
import com.johnson.habit.entity.Habit;
import com.johnson.habit.response.SuccessResponse;

public interface AIHabitGenerationService {
    SuccessResponse<Habit> generateHabitForUser(String token, GoalProfileDTO profile);
}
