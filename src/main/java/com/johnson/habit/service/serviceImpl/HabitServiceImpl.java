package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.entity.Habit;
import com.johnson.habit.repository.HabitRepository;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.response.exception.DefaultErrorException;
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
    public SuccessResponse<Habit> createHabit(Habit habit) {
        SuccessResponse<Habit> response = new SuccessResponse<>();
        try {
            habitRepository.save(habit);
            response.setMessage("Habit created");
            response.setStatusCode(200);
            response.setData(habit);
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            throw new DefaultErrorException("Error while creating Habit " +  e.getMessage());
        }
        return response;
    }

    @Override
    public void deleteHabit(UUID id) {
        Optional<Habit> optionalHabit = habitRepository.findById(id);
        assert optionalHabit.orElse(null) != null;
        habitRepository.delete(optionalHabit.orElse(null));

    }

    @Override
    public SuccessResponse<Habit> updateHabit(Habit habit) {
        SuccessResponse<Habit> response = new SuccessResponse<>();
        try {
            Optional<Habit> optionalHabit = habitRepository.findById(habit.getHabitId());
           if(optionalHabit.isEmpty()) {
               response.setStatusCode(404);
               response.setData(null);
               response.setMessage("Habit with habit id not found ");
           }
           else{

           response.setStatusCode(200);
           response.setData(habit);
           response.setMessage("Habit updated");}
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            throw new DefaultErrorException("Error while updating Habit " +  e.getMessage());
        }
        return response;

    }

    @Override
    public SuccessResponse<Habit> getHabit(UUID id) {
        SuccessResponse<Habit> response = new SuccessResponse<>();
        try {
            Optional<Habit> optionalHabit = habitRepository.findById(id);
            if (optionalHabit.isEmpty()) {
                response.setStatusCode(404);
                response.setData(null);
                response.setMessage("Habit with habit id not found ");
            }
            else {
                response.setStatusCode(200);
                response.setData(optionalHabit.get());
                response.setMessage("Habit found");
            }
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            throw new DefaultErrorException("Error while retrieving Habit " +  e.getMessage());
        }
        return response;
    }


}
