package com.johnson.habit.service;

import com.johnson.habit.entity.Habit;
import com.johnson.habit.response.SuccessResponse;

import java.util.UUID;

public interface HabitService {
    SuccessResponse<Habit> createHabit(Habit habit);
    void deleteHabit(UUID id);
    SuccessResponse<Habit> updateHabit(Habit habit);
    SuccessResponse<Habit> getHabit(UUID id);

}
