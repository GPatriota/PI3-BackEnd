package com.habitus.apipi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordCreateRequest {
    private Long userId;
    private Long habitId;
    private BigDecimal value;
    private OffsetDateTime date; 
}
