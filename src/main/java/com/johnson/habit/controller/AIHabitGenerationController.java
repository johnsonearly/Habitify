package com.johnson.habit.controller;

import com.johnson.habit.dto.GoalProfileDTO;
import com.johnson.habit.entity.Habit;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.service.AIHabitGenerationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/habit")
@RequiredArgsConstructor
public class AIHabitGenerationController {

    private final AIHabitGenerationService aiHabitGenerationService;

    @PostMapping("/generate-habits")
    public SuccessResponse<Habit> generateHabitsForUser(HttpServletRequest request, @RequestBody GoalProfileDTO profile) {
        String authHeader =  request.getHeader("Authorization");
        return aiHabitGenerationService.generateHabitForUser(authHeader, profile);
    }



}
