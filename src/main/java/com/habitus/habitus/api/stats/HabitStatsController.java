package com.habitus.habitus.api.stats;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/habits/{id}/stats")
@AllArgsConstructor
public class HabitStatsController {

    private HabitRepository repository;
    private StatsService service;

    @Operation(summary = "Получить статистику привычки")
    @GetMapping()
    public Result<HabitStatsData> getStats(
            @AuthenticationPrincipal UserDetailsInfo user,
            @PathVariable Long id
    ) {
        var habit = repository.findByIdAndOwner(id, user.getUser()).orElseThrow();
        var stats = habit.getStats().getLastUpdate() == null || habit.getStats().getLastUpdate().isBefore(LocalDate.now()) ?
                service.updateStats(habit) : habit.getStats();
        return Result.ok(toHabitStatsData(stats));
    }

    @Operation(summary = "Получить статистику привычки за период")
    @GetMapping("/period")
    public Result<HabitStatsData> getStatsPeriod(
            @AuthenticationPrincipal UserDetailsInfo user,
            @PathVariable Long id,

            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate startDate,

            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        var habit = repository.findByIdAndOwner(id, user.getUser()).orElseThrow();
        var stats = service.getStats(habit, startDate, endDate);
        return Result.ok(toHabitStatsData(stats));
    }

    private HabitStatsData toHabitStatsData(HabitStats stats) {
        return HabitStatsData.builder()
                .completion(stats.getCompletion())
                .completionCount(stats.getCompletionCount())
                .weekCompletion(stats.getWeekCompletion())
                .currentStreak(stats.getCurrentStreak())
                .currentMiss(stats.getCurrentMiss())
                .maxStreak(stats.getMaxStreak())
                .maxMiss(stats.getMaxMiss())
                .numData(stats.getMax() == null ? null :
                        HabitStatsData.NumData.builder()
                                .max(stats.getMax())
                                .min(stats.getMin())
                                .avg(stats.getAvg())
                                .sum(stats.getSum())
                                .build()
                        )
                .timeData(stats.getMaxTime() == null ? null :
                        HabitStatsData.TimeData.builder()
                                .max(stats.getMaxTime())
                                .min(stats.getMinTime())
                                .avg(stats.getAvgTime())
                                .sum(stats.getSumTime())
                                .build()
                        )
                .textData(stats.getTopValues() == null ? null :
                        HabitStatsData.TextData.builder()
                                .topValues(stats.getTopValues()
                                        .entrySet()
                                        .stream()
                                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                (a, b) -> a,
                                                LinkedHashMap::new // сохраняем порядок
                                        )))
                                .build()
                        )
                .build();
    }
}
