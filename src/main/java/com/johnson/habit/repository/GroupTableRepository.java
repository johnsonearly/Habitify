package com.johnson.habit.repository;

import com.johnson.habit.entity.GroupTable;
import com.johnson.habit.entity.enums.HabitCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupTableRepository extends JpaRepository<GroupTable, UUID> {
    @Query("SELECT p from  GroupTable p WHERE p.category =:category AND p.noMembers <4")
    Optional<GroupTable> findByCategory(@Param("category") HabitCategory habitCategory);

    Optional<GroupTable> findByHabitCategory(HabitCategory habitCategory);
    Optional<GroupTable> findByCategoryAndMembers_User_Username(
            HabitCategory category,
            String username
    );
}
