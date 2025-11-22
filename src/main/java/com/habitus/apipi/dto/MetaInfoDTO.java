package com.habitus.apipi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaInfoDTO {
    private String name;
    private String unit;
    private BigDecimal dailyGoal;
}
