package com.johnson.habit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "milestones")
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;
    private String title;
    private int targetDay;
    private boolean isCompleted;
    private LocalDateTime completedAt;
}