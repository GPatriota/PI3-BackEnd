package com.habitus.apipi.service;

import com.habitus.apipi.dto.UserHabitCreateRequest;
import com.habitus.apipi.dto.UserHabitUpdateRequest;
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

        if (measurementUnitId != null) {
            if (measurementUnitId == 2) {
                dailyGoal = dailyGoal.multiply(new BigDecimal("1000"));
                measurementUnitId = 1L;
            } else if (measurementUnitId == 4) {
                dailyGoal = dailyGoal.multiply(new BigDecimal("60"));
                measurementUnitId = 3L;
            }
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
    public Optional<UserHabit> update(Long id, UserHabitUpdateRequest request) {
        Optional<UserHabit> userHabitOpt = userHabitRepository.findById(id);
        
        if (userHabitOpt.isEmpty()) {
            return Optional.empty();
        }
        
        UserHabit userHabit = userHabitOpt.get();
        
        // Atualiza os campos se foram fornecidos
        if (request.getMeasurementUnitId() != null) {
            BigDecimal dailyGoal = request.getDailyGoal() != null ? request.getDailyGoal() : userHabit.getDailyGoal();
            Long measurementUnitId = request.getMeasurementUnitId();
            
            // Aplica conversões de unidade
            if (measurementUnitId == 2) {
                dailyGoal = dailyGoal.multiply(new BigDecimal("1000"));
                measurementUnitId = 1L;
            } else if (measurementUnitId == 4) {
                dailyGoal = dailyGoal.multiply(new BigDecimal("60"));
                measurementUnitId = 3L;
            }
            
            userHabit.setMeasurementUnitId(measurementUnitId);
            userHabit.setDailyGoal(dailyGoal);
        } else if (request.getDailyGoal() != null) {
            userHabit.setDailyGoal(request.getDailyGoal());
        }
        
        if (request.getWeeklyFrequency() != null) {
            userHabit.setWeeklyFrequency(request.getWeeklyFrequency());
        }
        
        if (request.getStartDate() != null) {
            userHabit.setStartDate(request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            userHabit.setEndDate(request.getEndDate());
        }
        
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

    @Transactional
    public boolean deleteByIdAndUserId(Long id, Long userId) {
        Optional<UserHabit> userHabitOpt = userHabitRepository.findById(id);
        
        if (userHabitOpt.isEmpty()) {
            return false;
        }
        
        UserHabit userHabit = userHabitOpt.get();
        
        // Valida se o UserHabit pertence ao usuário
        if (!userHabit.getUserId().equals(userId)) {
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

    @Transactional(readOnly = true)
    public List<UserHabitSummaryDTO> findWithFilters(Long userId, Long habitId) {
        List<UserHabit> userHabits;
        
        if (userId != null && habitId != null) {
            userHabits = userHabitRepository.findByUserIdAndHabitId(userId, habitId);
        } else if (userId != null) {
            userHabits = userHabitRepository.findByUserId(userId);
        } else if (habitId != null) {
            userHabits = userHabitRepository.findByHabitId(habitId);
        } else {
            userHabits = userHabitRepository.findAll();
        }
        
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
