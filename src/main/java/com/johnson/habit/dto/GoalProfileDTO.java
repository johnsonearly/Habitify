package com.johnson.habit.dto;

import com.johnson.habit.entity.enums.GoalType;
import com.johnson.habit.entity.enums.PersonalitySignal;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class GoalProfileDTO {
    private String rawGoalDescription;

//    private int hoursPerDayAvailable;
//
//    @Enumerated(EnumType.STRING)
//    private PersonalitySignal personalitySignal;
}
