package com.habitus.apipi.controller;

import com.habitus.apipi.dto.UserHabitCreateRequest;
import com.habitus.apipi.dto.UserHabitUpdateRequest;
import com.habitus.apipi.dto.UserHabitSummaryDTO;
import com.habitus.apipi.entity.UserHabit;
import com.habitus.apipi.service.UserHabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-habits")
@RequiredArgsConstructor
public class UserHabitController {

    private final UserHabitService userHabitService;

    @GetMapping
    public ResponseEntity<List<UserHabitSummaryDTO>> findAll(
            @RequestParam Long userId,
            @RequestParam(required = false) Long habitId) {
        List<UserHabitSummaryDTO> userHabits = userHabitService.findWithFilters(userId, habitId);
        return ResponseEntity.ok(userHabits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserHabit> findById(@PathVariable Long id) {
        return userHabitService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserHabit> create(@RequestBody UserHabitCreateRequest request) {
        UserHabit createdUserHabit = userHabitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserHabit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserHabit> update(@PathVariable Long id, @RequestBody UserHabitUpdateRequest request) {
        return userHabitService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        boolean deleted = userHabitService.deleteByIdAndUserId(id, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
