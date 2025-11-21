package com.habitus.apipi.repository;

import com.habitus.apipi.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    
    @Query("SELECT r FROM Record r " +
           "JOIN UserHabit uh ON r.userHabitId = uh.id " +
           "WHERE uh.userId = :userId " +
           "AND CAST(r.date AS date) = :date")
    List<Record> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
}