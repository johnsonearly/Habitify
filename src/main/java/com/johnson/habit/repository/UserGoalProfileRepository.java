package com.johnson.habit.repository;

import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.entity.UserGoalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserGoalProfileRepository extends JpaRepository<UserGoalProfile, UUID> {
    List<UserGoalProfile> findByIsMatchedFalseAndUserNot(UserEntity user);
}
