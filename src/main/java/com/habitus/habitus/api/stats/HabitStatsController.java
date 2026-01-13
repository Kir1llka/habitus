package com.habitus.habitus.api.stats;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/habits/{id}")
@AllArgsConstructor
public class HabitStatsController {

    private HabitRepository repository;
    private StatsService service;

    @Operation(summary = "Получить статистику привычки")
    @GetMapping("/stats")
    public Result<HabitStatsData> getStats(
            @AuthenticationPrincipal UserDetailsInfo user,
            @PathVariable Long id
    ) {
        var habit = repository.findByIdAndOwner(id, user.getUser()).orElseThrow();
        var stats = habit.getStats().getLastUpdate() == null || habit.getStats().getLastUpdate().isBefore(LocalDate.now()) ?
                service.updateStats(habit) : habit.getStats();
        return Result.ok(HabitStatsData.builder()
                .completion(stats.getCompletion())
                .completionCount(stats.getCompletionCount())
                .weekCompletion(stats.getWeekCompletion())
                .currentStreak(stats.getCurrentStreak())
                .currentMiss(stats.getCurrentMiss())
                .maxStreak(stats.getMaxStreak())
                .maxMiss(stats.getMaxMiss())
                .build());
    }
}
