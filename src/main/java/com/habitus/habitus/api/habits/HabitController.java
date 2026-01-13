package com.habitus.habitus.api.habits;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Result<HabitData> getHabit(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,
            @PathVariable Long id
    ) {
        return Result.ok(service.getHabit(userDetails.getUser(), id));
    }

    @Operation(summary = "Добавить новую привычку")
    @PostMapping()
    public Result<Void> addHabit(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,
            @Valid @RequestBody NewHabitData data
    ) {
        service.addHabit(userDetails.getUser(), data);
        return Result.ok();
    }

    @Operation(summary = "Изменить параметры привычки")
    @PostMapping("/configure")
    public Result<Void> configureHabit(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,
            @Valid @RequestBody ConfigureHabitData data
    ) {
        service.configureHabit(userDetails.getUser(), data);
        return Result.ok();
    }

    @Operation(summary = "Удалить привычку")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHabit(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,
            @PathVariable Long id
    ) {
        service.deleteHabit(userDetails.getUser(), id);
        return Result.ok();
    }
}
