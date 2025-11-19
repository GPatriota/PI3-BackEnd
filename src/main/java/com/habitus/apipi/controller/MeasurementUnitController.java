package com.habitus.apipi.controller;

import com.habitus.apipi.entity.MeasurementUnit;
import com.habitus.apipi.service.MeasurementUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/measurement-units")
@RequiredArgsConstructor
public class MeasurementUnitController {

    private final MeasurementUnitService measurementUnitService;

    @GetMapping
    public ResponseEntity<List<MeasurementUnit>> findAll() {
        return ResponseEntity.ok(measurementUnitService.findAll());
    }
}
