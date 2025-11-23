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
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com o email: " + user.getEmail());
        }

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
    public Optional<User> updatePartial(Long id, String name, String email, String oldPassword, String newPassword) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        User entity = opt.get();

        if (email != null && !email.equals(entity.getEmail())) {
            User existing = userRepository.findByEmail(email);
            if (existing != null && !existing.getId().equals(id)) {
                throw new IllegalArgumentException("Email já cadastrado por outro usuário");
            }
            entity.setEmail(email);
        }

        if (name != null) {
            entity.setName(name);
        }

        boolean wantsPasswordChange = oldPassword != null || newPassword != null;
        if (wantsPasswordChange) {
            if (oldPassword == null || newPassword == null) {
                throw new IllegalArgumentException("Para alterar a senha envie oldPassword e newPassword");
            }
    
            if (!entity.getPassword().equals(oldPassword)) {
                throw new IllegalArgumentException("Senha atual incorreta");
            }

            if (newPassword.trim().length() < 8) {
                throw new IllegalArgumentException("A nova senha deve ter pelo menos 8 caracteres");
            }
            entity.setPassword(newPassword);
        }

        return Optional.of(userRepository.save(entity));
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
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

}
