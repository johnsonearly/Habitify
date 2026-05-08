package com.johnson.habit.controller;

import com.johnson.habit.entity.Milestone;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.service.MileStoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class MileStoneController {
    private final MileStoneService mileStoneService;

    @GetMapping("get-milestone/{id}")
    public SuccessResponse<Milestone> getMilestoneById(@PathVariable UUID id) {
        return mileStoneService.getMilestoneById(id);
    }
    @PutMapping("update-milestone/{id}")
    public SuccessResponse<Milestone> updateMilestoneById(@PathVariable UUID id, @RequestBody Milestone  milestone) {
        milestone.setId(id);
        return mileStoneService.updateMilestone(milestone);
    }
    @PostMapping("create-milestone")
    public SuccessResponse<Milestone> createMilestone(@RequestBody Milestone milestone){
        return mileStoneService.createMilestone(milestone);
    }
}
