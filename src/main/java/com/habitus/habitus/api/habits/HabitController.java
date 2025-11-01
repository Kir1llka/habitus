package com.habitus.habitus.api.habits;

import com.habitus.habitus.service.HabitService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/habits")
@AllArgsConstructor
public class HabitController {

    private HabitService service;

    @GetMapping("/{id}")
    public HabitData getHabit(@PathVariable Long id) {
        var habit = service.getHabit(id);
        return HabitData.builder()
                .id(habit.getId())
                .name(habit.getName())
                .type(habit.getType())
                .build();
    }

    @PostMapping()
    public void addHabit(@RequestBody NewHabitData data) {
        service.addHabit(data);
    }

    @DeleteMapping("/{id}")
    public void deleteHabit(@PathVariable Long id) {
        service.deleteHabit(id);
    }
}
