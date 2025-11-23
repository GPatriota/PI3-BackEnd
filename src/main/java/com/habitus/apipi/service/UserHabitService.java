package com.habitus.apipi.service;

import com.habitus.apipi.dto.UserHabitCreateRequest;
import com.habitus.apipi.dto.UserHabitSummaryDTO;
import com.habitus.apipi.entity.Habit;
import com.habitus.apipi.entity.MeasurementUnit;
import com.habitus.apipi.entity.UserHabit;
import com.habitus.apipi.repository.HabitRepository;
import com.habitus.apipi.repository.MeasurementUnitRepository;
import com.habitus.apipi.repository.UserHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public UserHabit create(UserHabitCreateRequest request) {
        Optional<UserHabit> existingUserHabit = userHabitRepository
                .findByUserIdAndHabitIdAndEndDateIsNull(request.getUserId(), request.getHabitId());

        if (existingUserHabit.isPresent()) {
            UserHabit existing = existingUserHabit.get();
            existing.setEndDate(LocalDate.now());
            userHabitRepository.save(existing);
        }

        BigDecimal dailyGoal = request.getDailyGoal();
        Long measurementUnitId = request.getMeasurementUnitId();

        if (measurementUnitId == 2) {
            dailyGoal = dailyGoal.multiply(new BigDecimal("1000"));
            measurementUnitId = 1L;
        } else if (measurementUnitId == 4) {
            dailyGoal = dailyGoal.multiply(new BigDecimal("60"));
            measurementUnitId = 3L;
        }

        UserHabit newUserHabit = new UserHabit();
        newUserHabit.setUserId(request.getUserId());
        newUserHabit.setHabitId(request.getHabitId());
        newUserHabit.setMeasurementUnitId(measurementUnitId);
        newUserHabit.setDailyGoal(dailyGoal);
        newUserHabit.setWeeklyFrequency(request.getWeeklyFrequency());
        newUserHabit.setStartDate(LocalDate.now());
        newUserHabit.setEndDate(null);

        return userHabitRepository.save(newUserHabit);
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

    @Transactional(readOnly = true)
    public List<UserHabitSummaryDTO> findByUserId(Long userId) {
        List<UserHabit> userHabits = userHabitRepository.findByUserId(userId);
        return userHabits.stream()
                .map(uh -> new UserHabitSummaryDTO(
                        uh.getId(),
                        uh.getUserId(),
                        uh.getHabitId(),
                        uh.getHabit() != null ? uh.getHabit().getName() : null,
                        uh.getMeasurementUnitId(),
                        uh.getMeasurementUnit() != null ? uh.getMeasurementUnit().getSymbol() : null,
                        uh.getDailyGoal(),
                        uh.getWeeklyFrequency(),
                        uh.getStartDate(),
                        uh.getEndDate()))
                .collect(Collectors.toList());
    }

}
