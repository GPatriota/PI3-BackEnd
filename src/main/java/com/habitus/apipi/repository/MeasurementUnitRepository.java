package com.habitus.apipi.repository;

import com.habitus.apipi.entity.MeasurementUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long> {
    java.util.Optional<MeasurementUnit> findByName(String name);
    java.util.Optional<MeasurementUnit> findBySymbol(String symbol);
}
