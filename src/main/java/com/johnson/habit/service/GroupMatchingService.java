package com.johnson.habit.service;

import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.entity.UserGoalProfile;

public interface GroupMatchingService {
    void attemptSquadMatch(UserEntity user, UserGoalProfile newProfile);
}
