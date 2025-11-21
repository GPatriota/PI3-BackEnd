package com.habitus.apipi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitSummaryDTO {
    private Long id;
    private Long userId;
    private Long habitId;
    private String habitName;
    private Long measurementUnitId;
    private String measurementUnitSymbol;
    private BigDecimal dailyGoal;
    private Short weeklyFrequency;
    private LocalDate startDate;
    private LocalDate endDate;
}
