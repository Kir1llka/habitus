package com.habitus.habitus.api.habits;

import com.habitus.habitus.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Operation(summary = "Получить инфо о привычке")
    @GetMapping("/{id}")
    public HabitData getHabit(@PathVariable Long id) {
        var habit = service.getHabit(id);
        return HabitData.builder()
                .id(habit.getId())
                .name(habit.getName())
                .type(habit.getType())
                .hidden(habit.isHidden())
                .build();
    }

    @Operation(summary = "Добавить новую привычку")
    @PostMapping()
    public void addHabit(@Valid @RequestBody NewHabitData data) {
        service.addHabit(data);
    }

    @Operation(summary = "Изменить параметры привычки")
    @PostMapping("/configure")
    public void configureHabit(@Valid @RequestBody ConfigureHabitData data) {
        service.configureHabit(data);
    }

    @Operation(summary = "Удалить привычку")
    @DeleteMapping("/{id}")
    public void deleteHabit(@PathVariable Long id) {
        service.deleteHabit(id);
    }
}
