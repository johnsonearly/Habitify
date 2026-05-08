package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.entity.Milestone;
import com.johnson.habit.repository.MileStoneRepository;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.service.MileStoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MileStoneServiceImpl implements MileStoneService {
    private  final MileStoneRepository milestoneRepository;


    public SuccessResponse<Milestone> createMilestone(Milestone milestone) {
         milestoneRepository.save(milestone);
         SuccessResponse<Milestone> response = new SuccessResponse<>();
         response.setData(milestone);
         response.setMessage("Milestone created successfully");
         response.setStatusCode(200);
         return response;
    }

    public SuccessResponse<Milestone> getMilestoneById(UUID id) {
        Optional<Milestone> milestone = milestoneRepository.findById(id);
        SuccessResponse<Milestone> response = new SuccessResponse<>();
        response.setData(null);
        response.setMessage("Milestone can't be found");
        if (milestone.isPresent()) {
            response.setData(milestone.get());
            response.setMessage("Milestone found successfully");

        }
        return response;
    }

    public SuccessResponse<Milestone> updateMilestone(Milestone milestone) {
        Optional<Milestone> milestoneOptional = milestoneRepository.findById(milestone.getId());
        SuccessResponse<Milestone> response = new SuccessResponse<>();
        response.setData(null);
        response.setMessage("Milestone cant be found");
        if(milestoneOptional.isPresent()) {
            milestoneRepository.save(milestone);
            response.setData(milestoneOptional.get());
            response.setMessage("Milestone updated successfully");
        }
        return response;
    }

    public SuccessResponse<Milestone> deleteMilestone(UUID id) {
        Optional<Milestone> milestoneOptional = milestoneRepository.findById(id);
        SuccessResponse<Milestone> response = new SuccessResponse<>();
        response.setData(null);
        response.setMessage("Milestone cant be found");
        if(milestoneOptional.isPresent()) {
            milestoneRepository.delete(milestoneOptional.get());
            response.setData(milestoneOptional.get());
            response.setMessage("Milestone deleted successfully");

        }
        return response;
    }




}
