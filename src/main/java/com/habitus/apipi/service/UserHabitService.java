package com.habitus.apipi.service;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserHabitService {

    private final UserHabitRepository userHabitRepository;
    private final HabitRepository habitRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final MeasurementUnitService measurementUnitService;

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
        if (userHabit.getHabitId() == null) {
            throw new IllegalArgumentException("Habit ID cannot be null");
        }

        Optional<UserHabit> existingActive = userHabitRepository.findByUserIdAndHabitIdAndEndDateIsNull(
                userHabit.getUserId(),
                userHabit.getHabitId());

        if (existingActive.isPresent()) {
            UserHabit activeHabit = existingActive.get();
            activeHabit.setEndDate(LocalDate.now());
            userHabitRepository.save(activeHabit);
        }

        // Regra de negócio: Conversão de unidades
        Optional<Habit> habitOpt = habitRepository.findById(userHabit.getHabitId());
        
        if (habitOpt.isPresent()) {
            // Se a unidade de medida não for informada, usa a padrão do hábito
            if (userHabit.getMeasurementUnitId() == null) {
                userHabit.setMeasurementUnitId(habitOpt.get().getMeasurementUnitId());
            }
        }

        if (userHabit.getMeasurementUnitId() != null) {
            Optional<MeasurementUnit> unitOpt = measurementUnitRepository.findById(userHabit.getMeasurementUnitId());

            if (habitOpt.isPresent() && unitOpt.isPresent()) {
                measurementUnitService.applyConversionRules(userHabit, habitOpt.get(), unitOpt.get());
            }
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
                uh.getEndDate()
            ))
            .collect(Collectors.toList());
    }

}
