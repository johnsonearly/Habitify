package com.johnson.habit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;
    private String avatarUrl;
    private int totalPoints;
    private int level;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Habit> habits;
    private int xp;
    private String password;
    private LocalDateTime createdAt;

}
