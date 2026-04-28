package com.johnson.habit.service;

import com.johnson.habit.entity.Habit;

import java.util.UUID;

public interface HabitService {
    Habit createHabit(Habit habit);
    void deleteHabit(UUID id);
    void updateHabit(Habit habit);
    Habit getHabit(UUID id);

}
