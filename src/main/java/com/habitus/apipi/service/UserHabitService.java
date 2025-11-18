package com.habitus.apipi.service;

import com.habitus.apipi.entity.UserHabit;
import com.habitus.apipi.repository.UserHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserHabitService {

    private final UserHabitRepository userHabitRepository;

    @Transactional(readOnly = true)
    public List<UserHabit> findAll() {
        return userHabitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<UserHabit> findById(Long id) {
        return userHabitRepository.findById(id);
    }

    @Transactional
    public UserHabit create(UserHabit userHabit) {
        Optional<UserHabit> existingActive = userHabitRepository.findByUserIdAndHabitIdAndEndDateIsNull(
                userHabit.getUserId(),
                userHabit.getHabitId());

        if (existingActive.isPresent()) {
            UserHabit activeHabit = existingActive.get();
            activeHabit.setEndDate(LocalDate.now());
            userHabitRepository.save(activeHabit);
        }

        userHabit.setId(null);
        userHabit.setStartDate(LocalDate.now());
        userHabit.setEndDate(null);

        return userHabitRepository.save(userHabit);
    }

    @Transactional
    public Optional<UserHabit> update(Long id, UserHabit userHabit) {
        if (!userHabitRepository.existsById(id)) {
            return Optional.empty();
        }
        userHabit.setId(id);
        return Optional.of(userHabitRepository.save(userHabit));
    }

    @Transactional
    public boolean delete(Long id) {
        if (!userHabitRepository.existsById(id)) {
            return false;
        }
        userHabitRepository.deleteById(id);
        return true;
    }

}
