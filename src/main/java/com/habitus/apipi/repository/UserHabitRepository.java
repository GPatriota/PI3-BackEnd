package com.habitus.apipi.repository;

import com.habitus.apipi.entity.UserHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHabitRepository extends JpaRepository<UserHabit, Long> {
}
