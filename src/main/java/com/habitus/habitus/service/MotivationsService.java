package com.habitus.habitus.service;

import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.repository.entity.HabitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class MotivationsService {

    private StatsService statsService;

    public List<Motivation> getMotivations(Habit habit, Object value) {
        try {
            var stats = statsService.checkAndUpdateStats(habit);
            return getMotivationsExceptional(habit.getType(), stats, value);
        }
        catch (Exception e) {
            log.error("Failed getting motivations", e);
        }
        return new ArrayList<>();
    }

    private List<Motivation> getMotivationsExceptional(HabitType type, HabitStats stats, Object value) {
        var list = new ArrayList<Motivation>();

        if (value != null) {
            //streaks
            if (stats.getCurrentStreak() >= 3) list.add(new Motivation(stats.getCurrentStreak(), MotivationType.STREAK_N));
            if (stats.getCurrentStreak().equals(stats.getMaxStreak())) list.add(new Motivation(MotivationType.MAX_STREAK));
            if (stats.getCurrentStreak() == 1 && stats.getMaxMiss() > 0) list.add(new Motivation(MotivationType.COME_BACK));

            //counts
            if (stats.getCompletionCount() == 1) list.add(new Motivation(MotivationType.BEGINNING));
            if (stats.getCompletionCount() == 10) list.add(new Motivation(10, MotivationType.FIRST_N));
            if (stats.getCompletionCount() == 100) list.add(new Motivation(100, MotivationType.FIRST_N));
            if (stats.getCompletionCount() == 1000) list.add(new Motivation(1000, MotivationType.FIRST_N));

            if (type == HabitType.NUMBER) {
                var num = (Double) value;
                if (Objects.equals(stats.getMax(), num)) list.add(new Motivation(MotivationType.RECORD_MAX));
                if (Objects.equals(stats.getMin(), num)) list.add(new Motivation(MotivationType.RECORD_MIN));
                if (stats.getSum() > 100 && stats.getSum() - num < 100) list.add(new Motivation(100, MotivationType.SUM_N));
                if (stats.getSum() > 1000 && stats.getSum() - num < 1000) list.add(new Motivation(1000, MotivationType.SUM_N));
                if (list.size() < 3 && stats.getAvg() < num) list.add(new Motivation(MotivationType.BETTER));
            }

            if (type == HabitType.TIME) {
                var time = (LocalTime) value;
                if (Objects.equals(stats.getMaxTime(), time)) list.add(new Motivation(MotivationType.RECORD_MAX));
                if (Objects.equals(stats.getMinTime(), time)) list.add(new Motivation(MotivationType.RECORD_MIN));
                var sum = Duration.ofSeconds(stats.getSumTime());
                if (sum.toHours() > 100 && sum.minusSeconds(time.toSecondOfDay()).toHours() < 100) list.add(new Motivation(100, MotivationType.SUM_N));
                if (sum.toHours() > 1000 && sum.minusSeconds(time.toSecondOfDay()).toHours() < 1000) list.add(new Motivation(1000, MotivationType.SUM_N));
                if (list.size() < 3 && stats.getAvgTime() < Duration.ofSeconds(time.toSecondOfDay()).toSeconds()) list.add(new Motivation(MotivationType.BETTER));
            }
        } else {
            //streaks
            if (stats.getCurrentStreak() >= 3) list.add(new Motivation(stats.getCurrentStreak(), MotivationType.DONE_N));
            if (stats.getCurrentStreak().equals(stats.getMaxStreak())) list.add(new Motivation(MotivationType.MAX_STREAK));
            if (stats.getCurrentStreak() == 1 && stats.getMaxMiss() > 0) list.add(new Motivation(MotivationType.GO_GO_GO));

            //miss
            if (stats.getCurrentMiss() >= 3) list.add(new Motivation(stats.getCurrentMiss(), MotivationType.MISS_N));
            if (stats.getCurrentMiss().equals(stats.getMaxMiss())) list.add(new Motivation(MotivationType.MAX_MISS));
        }

        return list;
    }

    @Data
    @AllArgsConstructor
    public static class Motivation {
        private Integer number;
        private MotivationType type;

        public Motivation(MotivationType type) {
            this.type = type;
        }
    }

    public static enum MotivationType {
        STREAK_N,       // текущий стрик
        MAX_STREAK,     // максимальный стрик
        COME_BACK,      // с возвращением (когда вернулся после пропусков)
        BEGINNING,      // первая сделанная привычка
        FIRST_N,        // первые 10-100-1000 выполнений

        RECORD_MAX,     // рекорд максимума
        RECORD_MIN,     // рекорд минимума
        SUM_N,          // ачивка суммы (100,1000 и тд)
        BETTER,         // лучше (больше) чем обычно

        DONE_N,         // текущий стрик когда ты сегодня еще не успел сделать
        GO_GO_GO,       // когда есть миссы в истории и ты сделал вчера но сегодня еще не успел

        MISS_N,         // стрик мисов
        MAX_MISS,       // рекордный стрик мисов
    }
}
