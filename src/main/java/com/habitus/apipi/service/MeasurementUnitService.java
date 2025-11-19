package com.habitus.apipi.service;

import com.habitus.apipi.repository.MeasurementUnitRepository;
import com.habitus.apipi.entity.MeasurementUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementUnitService {

    private final MeasurementUnitRepository measurementUnitRepository;

    @Transactional(readOnly = true)
    public List<MeasurementUnit> findAll() {
        return measurementUnitRepository.findAll();
    }

}
