package com.johnson.habit.entity;

import com.johnson.habit.entity.enums.GoalType;
import com.johnson.habit.entity.enums.PersonalitySignal;
import com.johnson.habit.entity.enums.UrgencyLevel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_goal_profile")
@Data
public class UserGoalProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String rawGoalDescription;

    private String goalArea; // e.g. "reading", "fitness", "consistency"

    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel;

    private int hoursPerDayAvailable;

    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    private PersonalitySignal personalitySignal;

    private boolean isMatched;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
