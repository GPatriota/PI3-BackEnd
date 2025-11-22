package com.habitus.apipi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMetricasResponse {
    private MetaInfoDTO info;
    private MetricasDTO metrics;
    private List<GraficoItemDTO> chart;
}
