package com.habitus.habitus.api.habits;

import com.habitus.habitus.api.Result;
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
    public Result<HabitData> getHabit(@PathVariable Long id) {
        var habit = service.getHabit(id);
        return Result.ok(HabitData.builder()
                .id(habit.getId())
                .name(habit.getName())
                .type(habit.getType())
                .hidden(habit.isHidden())
                .build());
    }

    @Operation(summary = "Добавить новую привычку")
    @PostMapping()
    public Result<Void> addHabit(@Valid @RequestBody NewHabitData data) {
        service.addHabit(data);
        return Result.ok();
    }

    @Operation(summary = "Изменить параметры привычки")
    @PostMapping("/configure")
    public Result<Void> configureHabit(@Valid @RequestBody ConfigureHabitData data) {
        service.configureHabit(data);
        return Result.ok();
    }

    @Operation(summary = "Удалить привычку")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHabit(@PathVariable Long id) {
        service.deleteHabit(id);
        return Result.ok();
    }
}
