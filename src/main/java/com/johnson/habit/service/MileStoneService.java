package com.johnson.habit.service;
import com.johnson.habit.entity.Milestone;
import com.johnson.habit.response.SuccessResponse;

import java.util.UUID;

public interface MileStoneService {
    SuccessResponse<Milestone> createMilestone(Milestone milestone);
    SuccessResponse<Milestone>  getMilestoneById(UUID id);
    SuccessResponse<Milestone>  updateMilestone(Milestone milestone);
    SuccessResponse<Milestone>  deleteMilestone(UUID id);

}