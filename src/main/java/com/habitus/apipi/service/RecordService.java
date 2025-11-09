package com.habitus.apipi.service;

import com.habitus.apipi.entity.Record;
import com.habitus.apipi.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

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
        record.setId(null); // Ensure it's a new record
        return recordRepository.save(record);
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

}
