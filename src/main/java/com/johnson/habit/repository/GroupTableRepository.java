package com.johnson.habit.repository;

import com.johnson.habit.entity.GroupTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupTableRepository extends JpaRepository<GroupTable, UUID> {
}
