package com.habitus.apipi.service;

import com.habitus.apipi.entity.Habit;
import com.habitus.apipi.entity.User;
import com.habitus.apipi.entity.UserHabit;
import com.habitus.apipi.repository.HabitRepository;
import com.habitus.apipi.repository.UserHabitRepository;
import com.habitus.apipi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final UserHabitRepository userHabitRepository;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User create(User user) {
        user.setId(null);
        User savedUser = userRepository.save(user);

        List<Habit> allHabits = habitRepository.findAll();

        for (Habit habit : allHabits) {
            UserHabit userHabit = new UserHabit();
            userHabit.setUserId(savedUser.getId());
            userHabit.setHabitId(habit.getId());
            userHabit.setMeasurementUnitId(habit.getMeasurementUnitId());
            userHabit.setDailyGoal(BigDecimal.ZERO);
            userHabit.setWeeklyFrequency((short) 0);
            userHabit.setStartDate(LocalDate.now());
            userHabit.setEndDate(null);

            userHabitRepository.save(userHabit);
        }

        return savedUser;
    }

    @Transactional
    public Optional<User> update(Long id, User user) {
        if (!userRepository.existsById(id)) {
            return Optional.empty();
        }
        user.setId(id);
        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public User auth(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null; // email não existe
        }

        if (!user.getPassword().equals(password)) {
            return null; // senha incorreta
        }

        return user; // login OK
    }

}
