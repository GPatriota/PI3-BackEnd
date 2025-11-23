package com.habitus.apipi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitUpdateRequest {
    private Long measurementUnitId;
    private BigDecimal dailyGoal;
    private Short weeklyFrequency;
    private LocalDate startDate;
    private LocalDate endDate;
}
