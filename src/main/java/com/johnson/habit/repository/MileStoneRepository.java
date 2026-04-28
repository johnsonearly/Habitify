package com.johnson.habit.repository;

import com.johnson.habit.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MileStoneRepository  extends JpaRepository<Milestone, Integer> {
}
