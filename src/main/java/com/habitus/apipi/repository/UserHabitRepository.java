package com.habitus.apipi.repository;

import com.habitus.apipi.entity.UserHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHabitRepository extends JpaRepository<UserHabit, Long> {
    Optional<UserHabit> findByUserIdAndHabitIdAndEndDateIsNull(Long userId, Long habitId);
    List<UserHabit> findByUserId(Long userId);
    List<UserHabit> findByUserIdAndHabitId(Long userId, Long habitId);
    List<UserHabit> findByHabitId(Long habitId);
}
