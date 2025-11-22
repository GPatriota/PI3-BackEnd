package com.habitus.apipi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMetricasResponse {
    private MetaInfoDTO metaInfo;
    private MetricasDTO metricas;
    private List<GraficoItemDTO> grafico;
}
