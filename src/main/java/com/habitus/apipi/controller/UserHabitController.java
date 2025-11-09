package com.habitus.apipi.controller;

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
    public ResponseEntity<List<UserHabit>> findAll() {
        List<UserHabit> userHabits = userHabitService.findAll();
        return ResponseEntity.ok(userHabits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserHabit> findById(@PathVariable Long id) {
        return userHabitService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserHabit> create(@RequestBody UserHabit userHabit) {
        UserHabit created = userHabitService.create(userHabit);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserHabit> update(@PathVariable Long id, @RequestBody UserHabit userHabit) {
        return userHabitService.update(id, userHabit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = userHabitService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
