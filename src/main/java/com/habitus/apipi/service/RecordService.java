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
        // Busca TODOS os UserHabits (ativos e inativos) do usuário para esse hábito
        List<UserHabit> allUserHabits = userHabitRepository.findByUserIdAndHabitId(userId, habitId);
        if (allUserHabits.isEmpty()) {
            return Optional.empty();
        }
        
        // Pega o primeiro para informações básicas (nome do hábito, unidade)
        UserHabit referenceUserHabit = allUserHabits.get(0);
        
        // Busca todos os registros de TODOS os UserHabits no período
        List<Record> allRecords = new ArrayList<>();
        for (UserHabit uh : allUserHabits) {
            List<Record> records = recordRepository.findByUserHabitIdAndDateRange(uh.getId(), dataInicio, dataFim);
            allRecords.addAll(records);
        }
        
        // Agrupa os registros por data e soma os valores
        // Converte para o fuso horário de São Paulo antes de extrair a data
        ZoneId saoPauloZone = ZoneId.of("America/Sao_Paulo");
        Map<LocalDate, BigDecimal> dailyTotals = allRecords.stream()
            .collect(Collectors.groupingBy(
                r -> r.getDate().atZoneSameInstant(saoPauloZone).toLocalDate(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Record::getValue,
                    BigDecimal::add
                )
            ));
        
        // Cria lista de datas completa (incluindo dias sem registro)
        List<GraficoItemDTO> chart = new ArrayList<>();
        LocalDate currentDate = dataInicio;
        while (!currentDate.isAfter(dataFim)) {
            BigDecimal total = dailyTotals.getOrDefault(currentDate, BigDecimal.ZERO);
            
            // Determina qual era a meta diária ativa nesta data
            BigDecimal dailyGoal = getDailyGoalForDate(allUserHabits, currentDate);
            
            chart.add(new GraficoItemDTO(currentDate, total, dailyGoal));
            currentDate = currentDate.plusDays(1);
        }
        
        // Calcula métricas
        BigDecimal weeklyAverage = BigDecimal.ZERO;
        BigDecimal bestRecord = BigDecimal.ZERO;
        
        if (!dailyTotals.isEmpty()) {
            BigDecimal totalSum = dailyTotals.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int daysWithRecords = dailyTotals.size();
            weeklyAverage = totalSum.divide(
                BigDecimal.valueOf(daysWithRecords), 
                2, 
                RoundingMode.HALF_UP
            );
            
            bestRecord = dailyTotals.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        }
        
        // Pega a meta diária atual (do UserHabit ativo ou do mais recente)
        UserHabit currentUserHabit = allUserHabits.stream()
            .filter(uh -> uh.getEndDate() == null)
            .findFirst()
            .orElse(allUserHabits.get(allUserHabits.size() - 1));
        
        // Monta a resposta
        MetaInfoDTO info = new MetaInfoDTO(
            referenceUserHabit.getHabit().getName(),
            referenceUserHabit.getMeasurementUnit().getSymbol(),
            currentUserHabit.getDailyGoal()
        );
        
        MetricasDTO metrics = new MetricasDTO(weeklyAverage, bestRecord);
        
        HistoricoMetricasResponse response = new HistoricoMetricasResponse(info, metrics, chart);
        
        return Optional.of(response);
    }
    
    private BigDecimal getDailyGoalForDate(List<UserHabit> userHabits, LocalDate date) {
        // Procura qual UserHabit estava ativo nesta data específica
        for (UserHabit uh : userHabits) {
            LocalDate startDate = uh.getStartDate();
            LocalDate endDate = uh.getEndDate();
            
            boolean isAfterOrEqualStart = startDate == null || !date.isBefore(startDate);
            boolean isBeforeOrEqualEnd = endDate == null || !date.isAfter(endDate);
            
            if (isAfterOrEqualStart && isBeforeOrEqualEnd) {
                return uh.getDailyGoal() != null ? uh.getDailyGoal() : BigDecimal.ZERO;
            }
        }
        
        // Se não encontrou nenhum UserHabit ativo nesta data, retorna 0
        return BigDecimal.ZERO;
    }

}
