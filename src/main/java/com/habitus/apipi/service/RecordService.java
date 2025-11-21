package com.habitus.apipi.service;

import com.habitus.apipi.dto.RecordCreateRequest;
import com.habitus.apipi.entity.Record;
import com.habitus.apipi.entity.UserHabit;
import com.habitus.apipi.repository.RecordRepository;
import com.habitus.apipi.repository.UserHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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

}
