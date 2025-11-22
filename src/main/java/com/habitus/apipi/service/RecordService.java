package com.habitus.apipi.service;

import com.habitus.apipi.dto.GraficoItemDTO;
import com.habitus.apipi.dto.HistoricoMetricasResponse;
import com.habitus.apipi.dto.MetaInfoDTO;
import com.habitus.apipi.dto.MetricasDTO;
import com.habitus.apipi.dto.RecordCreateRequest;
import com.habitus.apipi.entity.Record;
import com.habitus.apipi.entity.UserHabit;
import com.habitus.apipi.repository.RecordRepository;
import com.habitus.apipi.repository.UserHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserHabitRepository userHabitRepository;

    @Transactional(readOnly = true)
    public List<Record> findAll() {
        return recordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Record> findById(Long id) {
        return recordRepository.findById(id);
    }

    @Transactional
    public Record create(Record record) {
        record.setId(null);
        return recordRepository.save(record);
    }

    @Transactional
    public Optional<Record> createFromUserAndHabit(RecordCreateRequest req) {
        Optional<UserHabit> activeUH = userHabitRepository
                .findByUserIdAndHabitIdAndEndDateIsNull(req.getUserId(), req.getHabitId());

        if (activeUH.isEmpty()) {
            return Optional.empty();
        }

        Record record = new Record();
        record.setUserHabitId(activeUH.get().getId());
        record.setValue(req.getValue());
        record.setDate(req.getDate() != null ? req.getDate() : OffsetDateTime.now());
        record.setId(null);
        return Optional.of(recordRepository.save(record));
    }

    @Transactional
    public Optional<Record> update(Long id, Record record) {
        if (!recordRepository.existsById(id)) {
            return Optional.empty();
        }
        record.setId(id);
        return Optional.of(recordRepository.save(record));
    }

    @Transactional
    public boolean delete(Long id) {
        if (!recordRepository.existsById(id)) {
            return false;
        }
        recordRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Record> findByUserAndDate(Long userId, LocalDate date) {
        return recordRepository.findByUserIdAndDate(userId, date);
    }

    @Transactional(readOnly = true)
    public Optional<HistoricoMetricasResponse> getHistoricoByUserAndHabit(Long userId, Long habitId, LocalDate dataInicio, LocalDate dataFim) {
        // Busca o UserHabit ativo do usuário para esse hábito
        Optional<UserHabit> userHabitOpt = userHabitRepository.findByUserIdAndHabitIdAndEndDateIsNull(userId, habitId);
        if (userHabitOpt.isEmpty()) {
            return Optional.empty();
        }
        
        UserHabit userHabit = userHabitOpt.get();
        Long idUsuarioHabito = userHabit.getId();
        
        // Busca todos os registros no período
        List<Record> records = recordRepository.findByUserHabitIdAndDateRange(idUsuarioHabito, dataInicio, dataFim);
        
        // Agrupa os registros por data e soma os valores
        // Converte para o fuso horário de São Paulo antes de extrair a data
        ZoneId saoPauloZone = ZoneId.of("America/Sao_Paulo");
        Map<LocalDate, BigDecimal> dailyTotals = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getDate().atZoneSameInstant(saoPauloZone).toLocalDate(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Record::getValue,
                    BigDecimal::add
                )
            ));
        
        // Cria lista de datas completa (incluindo dias sem registro)
        List<GraficoItemDTO> grafico = new ArrayList<>();
        LocalDate currentDate = dataInicio;
        while (!currentDate.isAfter(dataFim)) {
            BigDecimal total = dailyTotals.getOrDefault(currentDate, BigDecimal.ZERO);
            grafico.add(new GraficoItemDTO(currentDate, total));
            currentDate = currentDate.plusDays(1);
        }
        
        // Calcula métricas
        BigDecimal mediaSemanal = BigDecimal.ZERO;
        BigDecimal melhorRegistro = BigDecimal.ZERO;
        
        if (!dailyTotals.isEmpty()) {
            BigDecimal somaTotal = dailyTotals.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int diasComRegistro = dailyTotals.size();
            mediaSemanal = somaTotal.divide(
                BigDecimal.valueOf(diasComRegistro), 
                2, 
                RoundingMode.HALF_UP
            );
            
            melhorRegistro = dailyTotals.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        }
        
        // Monta a resposta
        MetaInfoDTO metaInfo = new MetaInfoDTO(
            userHabit.getHabit().getName(),
            userHabit.getMeasurementUnit().getSymbol(),
            userHabit.getDailyGoal()
        );
        
        MetricasDTO metricas = new MetricasDTO(mediaSemanal, melhorRegistro);
        
        HistoricoMetricasResponse response = new HistoricoMetricasResponse(metaInfo, metricas, grafico);
        
        return Optional.of(response);
    }

}
