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

}
