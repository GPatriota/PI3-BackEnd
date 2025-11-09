package com.habitus.apipi.controller;

import com.habitus.apipi.service.MeasurementUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/measurement-units")
@RequiredArgsConstructor
public class MeasurementUnitController {

    private final MeasurementUnitService measurementUnitService;
    // TODO: Fazer aqui as services se precisar fazer depois pra unidades de medida
    // (como já sabemos todas, não vou fazer crud nem nada, só se precisar faz algo
    // aqui)
}
