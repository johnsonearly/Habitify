package com.johnson.habit.entity;

import com.johnson.habit.entity.enums.GroupCategory;
import com.johnson.habit.entity.enums.HabitCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID habitId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private HabitCategory category;
    private int targetDays;
    private int targetHours;
    private int targetMinutes;
    private int currentStreak;
    private int longestStreak;
    private boolean isActive;

}
