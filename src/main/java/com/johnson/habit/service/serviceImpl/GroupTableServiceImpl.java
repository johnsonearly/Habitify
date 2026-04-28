package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.entity.GroupTable;
import com.johnson.habit.repository.GroupTableRepository;
import com.johnson.habit.service.GroupTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupTableServiceImpl implements GroupTableService {
    private final GroupTableRepository groupTableRepository;


    @Override
    public void createGroup(GroupTable group) {
        groupTableRepository.save(group);
    }

    @Override
    public void updateGroup(GroupTable group) {
        Optional<GroupTable> groupTable = groupTableRepository.findById(group.getId());
        if (groupTable.isPresent()) {
            groupTableRepository.save(group);
        }

    }

    @Override
    public void deleteGroup(UUID id) {
        Optional<GroupTable> groupTable = groupTableRepository.findById(id);
        groupTable.ifPresent(groupTableRepository::delete);

    }

}
