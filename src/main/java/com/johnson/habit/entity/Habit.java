package com.johnson.habit.entity;

import com.johnson.habit.entity.enums.HabitCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
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
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<Milestone> milestones;

}
