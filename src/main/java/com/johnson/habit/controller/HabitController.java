package com.johnson.habit.controller;

import com.johnson.habit.entity.Habit;
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
    public ResponseEntity<String> createHabit(@RequestBody Habit habit) {
        habitService.createHabit(habitService.createHabit(habit));
        return ResponseEntity.ok("Habit created");
    }
    @PutMapping ("/update-habit/{id}")
    public ResponseEntity<String> updateHabit(@PathVariable UUID id, @RequestBody Habit habit) {
        habitService.updateHabit(habit);
        return ResponseEntity.ok("Habit updated");
    }
    @GetMapping("/get-habit/{id}")
    public Habit getHabit(@PathVariable UUID id) {
        return habitService.getHabit(id);
    }





}
