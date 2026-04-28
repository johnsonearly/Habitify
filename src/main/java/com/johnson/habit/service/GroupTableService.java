package com.johnson.habit.service;

import com.johnson.habit.entity.GroupTable;

import java.util.UUID;

public interface GroupTableService {
    void createGroup(GroupTable group);
    void updateGroup(GroupTable group);
    void deleteGroup(UUID id);
}
