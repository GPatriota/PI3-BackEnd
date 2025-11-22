package com.habitus.apipi.controller;

import com.habitus.apipi.dto.HistoricoMetricasResponse;
import com.habitus.apipi.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RecordService recordService;

    @GetMapping("/historico")
    public ResponseEntity<HistoricoMetricasResponse> getHistorico(
            @RequestParam Long userId,
            @RequestParam Long habitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        return recordService.getHistoricoByUserAndHabit(userId, habitId, dataInicio, dataFim)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
