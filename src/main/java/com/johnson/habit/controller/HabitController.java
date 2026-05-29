package com.johnson.habit.controller;

import com.johnson.habit.entity.Habit;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/habit")
public class HabitController {
    private final HabitService habitService;

    @PostMapping("/create-habit")
    public ResponseEntity<SuccessResponse<Habit>> createHabit(@RequestBody Habit habit) {
        return ResponseEntity.ok(habitService.createHabit(habit));
    }
    @PutMapping ("/update-habit/{id}")
    public ResponseEntity<SuccessResponse<Habit>> updateHabit(@PathVariable UUID id, @RequestBody Habit habit) {
        return ResponseEntity.ok(habitService.updateHabit(habit));
    }
    @GetMapping("/get-habit/{id}")
    public ResponseEntity<SuccessResponse<Habit>>  getHabit(@PathVariable UUID id) {
        return ResponseEntity.ok(habitService.getHabit(id));
    }





}
