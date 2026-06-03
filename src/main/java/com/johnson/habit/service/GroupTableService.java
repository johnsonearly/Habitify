package com.johnson.habit.service;

import com.johnson.habit.dto.GroupMembersDTO;
import com.johnson.habit.entity.GroupTable;
import com.johnson.habit.entity.Habit;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.entity.enums.HabitCategory;

import java.util.List;
import java.util.UUID;

public interface GroupTableService {
    void createGroup(GroupTable group);
    void updateGroup(GroupTable group);
    void deleteGroup(UUID id);
    void findGroupByHabitCategory(Habit habit, UserEntity userEntity);
    List<GroupMembersDTO> fetchGroupTableByUsernameAndHabitCategory(
            String username,
            HabitCategory category
    );
}
