package com.habitus.habitus.service;

import com.habitus.habitus.repository.BooleanRecordRepository;
import com.habitus.habitus.repository.HabitStatsRepository;
import com.habitus.habitus.repository.NumberRecordRepository;
import com.habitus.habitus.repository.TextRecordRepository;
import com.habitus.habitus.repository.TimeRecordRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.repository.entity.HabitType;
import com.habitus.habitus.repository.entity.ScheduleType;
import com.habitus.habitus.repository.entity.records.BooleanRecord;
import com.habitus.habitus.repository.entity.records.NumberRecord;
import com.habitus.habitus.repository.entity.records.TextRecord;
import com.habitus.habitus.repository.entity.records.TimeRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        var stats = getStats(habit, habit.getStartDate(), LocalDate.now());

        saveStats(habit, stats);
        return stats;
    }

    public HabitStats getStats(Habit habit, LocalDate startDate, LocalDate endDate) {

        var recordsData = getRecordsData(habit, startDate, endDate);
        List<LocalDate> doneDates = recordsData.getDoneDates();
        LocalDate date = startDate;
        var currentWeek = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        var countWeek = 0;
        int currentStreak = 0;
        int maxStreak = 0;
        int currentMiss = 0;
        int maxMiss = 0;

        while (!date.isAfter(endDate)) {

            if (doneDates.contains(date)) {
                // день выполнен
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);

                currentMiss = 0;

                if (date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == currentWeek) {
                    countWeek++;
                }
            } else if (date.isBefore(endDate)) {
                // день пропущен
                currentMiss++;
                maxMiss = Math.max(maxMiss, currentMiss);

                currentStreak = 0;
            }

            date = date.plusDays(1);
        }

        var stats = new HabitStats();
        stats.setCompletionCount(doneDates.size());

        if (habit.getSchedule() == ScheduleType.EVERYDAY) {
            stats.setCompletion((int) (doneDates.size() * 100 / Math.max(1, ChronoUnit.DAYS.between(startDate, endDate))));
            if (startDate.equals(habit.getStartDate()) && endDate.equals(LocalDate.now())) {
                stats.setWeekCompletion(countWeek);
                stats.setMaxStreak(maxStreak);
                stats.setMaxMiss(maxMiss);
                stats.setCurrentStreak(currentStreak);
                stats.setCurrentMiss(currentMiss);
            }
        }

        if (habit.getType() == HabitType.NUMBER) {
            stats.setMax(recordsData.integers.stream().max(Comparator.naturalOrder()).orElse(null));
            stats.setMin(recordsData.integers.stream().min(Comparator.naturalOrder()).orElse(null));
            stats.setSum(recordsData.integers.stream().mapToDouble(d -> d).sum());
            stats.setAvg(recordsData.integers.stream().mapToDouble(d -> d).average().orElse(0));
        }

        if (habit.getType() == HabitType.TIME) {
            stats.setMaxTime(recordsData.times.stream().max(Comparator.naturalOrder()).orElse(null));
            stats.setMinTime(recordsData.times.stream().min(Comparator.naturalOrder()).orElse(null));
            stats.setSumTime(recordsData.times.stream()
                    .map(time -> Duration.ofNanos(time.toNanoOfDay()))
                    .reduce(Duration.ZERO, Duration::plus).toSeconds());
            stats.setAvgTime(stats.getSumTime() / recordsData.getDoneDates().size());
        }

        if (habit.getType() == HabitType.TEXT) {
            var strings = recordsData.strings.stream().flatMap(s -> Arrays.stream(s.split(",")))
                    .map(String::trim)
                    .collect(Collectors.groupingBy(
                            Function.identity(),
                            Collectors.counting()
                    ));
            stats.setTopValues(strings);
        }
        return stats;
    }

    private RecordsData getRecordsData(Habit habit, LocalDate startDate, LocalDate endDate) {
        var data = new RecordsData();
        switch (habit.getType()) {
            case GENERAL -> {
                var recs = booleanRecordRepository.findByHabitAndId_RecordDateBetween(habit, startDate, endDate).stream()
                        .filter(r -> r.getPayload() == true)
                        .toList();
                data.setBooleans(recs.stream().map(BooleanRecord::getPayload).toList());
                data.setDoneDates(recs.stream().map(r -> r.getId().getRecordDate()).toList());
            }
            case NUMBER -> {
                var recs = numberRecordRepository.findByHabitAndId_RecordDateBetween(habit, startDate, endDate).stream()
                        .filter(r -> r.getPayload() != 0)
                        .toList();
                data.setIntegers(recs.stream().map(NumberRecord::getPayload).toList());
                data.setDoneDates(recs.stream().map(r -> r.getId().getRecordDate()).toList());
            }
            case TEXT -> {
                var recs = textRecordRepository.findByHabitAndId_RecordDateBetween(habit, startDate, endDate).stream()
                        .filter(r -> StringUtils.isNotBlank(r.getPayload()))
                        .toList();
                data.setStrings(recs.stream().map(TextRecord::getPayload).toList());
                data.setDoneDates(recs.stream().map(r -> r.getId().getRecordDate()).toList());
            }
            case TIME -> {
                var recs = timeRecordRepository.findByHabitAndId_RecordDateBetween(habit, startDate, endDate).stream()
                        .filter(r -> r.getPayload().toSecondOfDay() > 0)
                        .toList();
                data.setTimes(recs.stream().map(TimeRecord::getPayload).toList());
                data.setDoneDates(recs.stream().map(r -> r.getId().getRecordDate()).toList());
            }
            default -> throw new RuntimeException();
        }
        return data;
    }

    private void saveStats(Habit habit, HabitStats stats) {
        stats.setId(habit.getStats().getId());
        stats.setHabit(habit);
        stats.setLastUpdate(LocalDate.now());
        repository.save(stats);
    }

    @Data
    private static class RecordsData {
        private List<LocalDate> doneDates;
        private List<Boolean> booleans;
        private List<Double> integers;
        private List<String> strings;
        private List<LocalTime> times;
    }
}
