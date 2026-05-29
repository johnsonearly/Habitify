package com.johnson.habit.controller;

import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.service.AIGroupingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-matching")
@RequiredArgsConstructor
public class AIGroupMatchingController {

    private final AIGroupingService aiGroupingService;


    @PostMapping("/{username}")
    public SuccessResponse processNewUser(@PathVariable String username, @RequestBody String goalDescription) {
        return aiGroupingService.processNewUser(username,goalDescription);
    }




}
