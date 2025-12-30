package com.habitus.habitus.service;

import com.habitus.habitus.repository.BooleanRecordRepository;
import com.habitus.habitus.repository.HabitStatsRepository;
import com.habitus.habitus.repository.NumberRecordRepository;
import com.habitus.habitus.repository.TextRecordRepository;
import com.habitus.habitus.repository.TimeRecordRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.repository.entity.HabitType;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsService {

    private HabitStatsRepository repository;
    private BooleanRecordRepository booleanRecordRepository;
    private TextRecordRepository textRecordRepository;
    private NumberRecordRepository numberRecordRepository;
    private TimeRecordRepository timeRecordRepository;

    public HabitStats updateStats(Habit habit) {
        if (habit.getEldestDate() == null) return habit.getStats();
        List<LocalDate> doneDates = getDoneDates(habit);
        LocalDate date = habit.getStartDate();
        var currentWeek = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        var countWeek = 0;
        int currentStreak = 0;
        int maxStreak = 0;
        int currentMiss = 0;
        int maxMiss = 0;

        while (!date.isAfter(LocalDate.now())) {

            if (doneDates.contains(date)) {
                // день выполнен
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);

                currentMiss = 0;

                if (date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == currentWeek) {
                    countWeek++;
                }
            } else if (date.isBefore(LocalDate.now())) {
                // день пропущен
                currentMiss++;
                maxMiss = Math.max(maxMiss, currentMiss);

                currentStreak = 0;
            }

            date = date.plusDays(1);
        }

        var stats = habit.getStats();
        stats.setCompletionCount(doneDates.size());
        stats.setCompletion((int) (doneDates.size() * 100 / ChronoUnit.DAYS.between(habit.getStartDate(), LocalDate.now())));
        stats.setWeekCompletion(countWeek);
        stats.setMaxStreak(maxStreak);
        stats.setMaxMiss(maxMiss);
        stats.setCurrentStreak(currentStreak);
        stats.setCurrentMiss(currentMiss);
        stats.setLastUpdate(LocalDate.now());
        repository.save(stats);
        return stats;
    }

    private List<LocalDate> getDoneDates(Habit habit) {
        return switch (habit.getType()) {
            case GENERAL ->
                    booleanRecordRepository.findByHabitAndId_RecordDateBetween(habit, habit.getStartDate(), LocalDate.now()).stream()
                            .filter(r -> isDone(habit.getType(), r.getPayload()))
                            .map(r -> r.getId().getRecordDate())
                            .toList();
            case NUMBER ->
                    numberRecordRepository.findByHabitAndId_RecordDateBetween(habit, habit.getStartDate(), LocalDate.now()).stream()
                            .filter(r -> isDone(habit.getType(), r.getPayload()))
                            .map(r -> r.getId().getRecordDate())
                            .toList();
            case TEXT ->
                    textRecordRepository.findByHabitAndId_RecordDateBetween(habit, habit.getStartDate(), LocalDate.now()).stream()
                            .filter(r -> isDone(habit.getType(), r.getPayload()))
                            .map(r -> r.getId().getRecordDate())
                            .toList();
            case TIME ->
                    timeRecordRepository.findByHabitAndId_RecordDateBetween(habit, habit.getStartDate(), LocalDate.now()).stream()
                            .filter(r -> isDone(habit.getType(), r.getPayload()))
                            .map(r -> r.getId().getRecordDate())
                            .toList();
            default -> throw new RuntimeException();
        };
    }

    private boolean isDone(HabitType type, Object value) {
        return switch (type) {
            case GENERAL -> value != null && ((Boolean) value);
            case NUMBER -> value != null && ((Double) value) != 0;
            case TEXT -> value != null && StringUtils.isNotBlank((String) value);
            case TIME -> value != null && ((LocalTime) value).toSecondOfDay() > 0;
        };
    }
}
