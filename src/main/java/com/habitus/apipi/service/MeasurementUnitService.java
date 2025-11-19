package com.habitus.apipi.service;

import com.habitus.apipi.entity.Habit;
import com.habitus.apipi.entity.MeasurementUnit;
import com.habitus.apipi.entity.UserHabit;
import com.habitus.apipi.repository.MeasurementUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeasurementUnitService {

    private final MeasurementUnitRepository measurementUnitRepository;

    @Transactional(readOnly = true)
    public List<MeasurementUnit> findAll() {
        return measurementUnitRepository.findAll();
    }

    public void applyConversionRules(UserHabit userHabit, Habit habit, MeasurementUnit currentUnit) {
        String habitName = habit.getName().toLowerCase();
        String unitName = currentUnit.getName().toLowerCase();

        // Caso 1: Água em Litros -> Mililitros
        if (habitName.contains("água") || habitName.contains("agua")) {
            if (unitName.contains("litro") && !unitName.contains("mili")) {
                userHabit.setDailyGoal(userHabit.getDailyGoal().multiply(new BigDecimal("1000")));

                Optional<MeasurementUnit> mlUnit = measurementUnitRepository.findByName("Mililitros");
                if (mlUnit.isEmpty()) {
                    mlUnit = measurementUnitRepository.findBySymbol("ml");
                }

                mlUnit.ifPresent(measurementUnit -> userHabit.setMeasurementUnitId(measurementUnit.getId()));
            }
        }

        // Caso 2: Sono em Horas -> Minutos
        if (habitName.contains("sono") || habitName.contains("dormir")) {
            if (unitName.contains("hora")) {
                userHabit.setDailyGoal(userHabit.getDailyGoal().multiply(new BigDecimal("60")));

                Optional<MeasurementUnit> minUnit = measurementUnitRepository.findByName("Minutos");
                if (minUnit.isEmpty()) {
                    minUnit = measurementUnitRepository.findBySymbol("min");
                }

                minUnit.ifPresent(measurementUnit -> userHabit.setMeasurementUnitId(measurementUnit.getId()));
            }
        }
        
    }

}
