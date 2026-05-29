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
    private String description;      // e.g. "Read 5 pages daily"
    private int dailyTarget;         // e.g. 5 pages, 20 mins, 1 video
    private String targetUnit;       // e.g. "pages", "minutes", "videos"
    private int startDay;            // which day of the habit this begins
    private int endDay;              // which day it ends
    private boolean isCompleted;
    private boolean isActive;        // is this the current milestone

    private LocalDateTime completedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}