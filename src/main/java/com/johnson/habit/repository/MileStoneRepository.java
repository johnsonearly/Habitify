package com.johnson.habit.repository;

import com.johnson.habit.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MileStoneRepository  extends JpaRepository<Milestone, UUID> {
    Optional<List<Milestone>> findAllByUserId(UUID userId);
}
