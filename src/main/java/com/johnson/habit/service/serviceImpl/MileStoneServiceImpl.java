package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.entity.Milestone;
import com.johnson.habit.repository.MileStoneRepository;
import com.johnson.habit.service.MileStoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MileStoneServiceImpl implements MileStoneService {
    private  final MileStoneRepository milestoneRepository;

    @Override
    public Milestone createMilestone(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }
    public Milestone getMilestoneById(UUID id) {

    }

}
