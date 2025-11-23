package com.habitus.apipi.service;

import com.habitus.apipi.entity.Habit;
import com.habitus.apipi.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;

    @Transactional(readOnly = true)
    public List<Habit> findAll() {
        return habitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Habit> findById(Long id) {
        return habitRepository.findById(id);
    }

    @Transactional
    public Habit create(Habit habit) {
        habit.setId(null);
        return habitRepository.save(habit);
    }

    @Transactional
    public Optional<Habit> update(Long id, Habit habit) {
        if (!habitRepository.existsById(id)) {
            return Optional.empty();
        }
        habit.setId(id);
        return Optional.of(habitRepository.save(habit));
    }

    @Transactional
    public boolean delete(Long id) {
        if (!habitRepository.existsById(id)) {
            return false;
        }
        habitRepository.deleteById(id);
        return true;
    }

}
