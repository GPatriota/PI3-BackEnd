package com.habitus.apipi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaInfoDTO {
    private String nome;
    private String unidade;
    private BigDecimal metaDiaria;
}
