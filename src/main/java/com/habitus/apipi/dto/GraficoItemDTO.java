package com.habitus.apipi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraficoItemDTO {
    private LocalDate date;
    private BigDecimal total;
    private BigDecimal dailyGoal;
}
