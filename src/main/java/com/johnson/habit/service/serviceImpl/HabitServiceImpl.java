package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.entity.Habit;
import com.johnson.habit.repository.HabitRepository;
import com.johnson.habit.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {
     private final HabitRepository habitRepository;

    @Override
    public Habit createHabit(Habit habit) {
        return habitRepository.save(habit);
    }

    @Override
    public void deleteHabit(UUID id) {
        Optional<Habit> optionalHabit = habitRepository.findById(id);
        assert optionalHabit.orElse(null) != null;
        habitRepository.delete(optionalHabit.orElse(null));

    }

    @Override
    public void updateHabit(Habit habit) {
        Optional<Habit> optionalHabit = habitRepository.findById(habit.getHabitId());
        assert optionalHabit.orElse(null) != null;
        habitRepository.save(optionalHabit.orElse(null));

    }

    @Override
    public Habit getHabit(UUID id) {
        Optional<Habit> optionalHabit = habitRepository.findById(id);
        if(optionalHabit.isPresent()) {
            habitRepository.findById(id);
        }
        return optionalHabit.orElse(null);
    }


}
