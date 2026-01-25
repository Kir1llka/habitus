package com.habitus.habitus.service;

import com.habitus.habitus.api.habits.ConfigureHabitData;
import com.habitus.habitus.api.habits.HabitData;
import com.habitus.habitus.api.habits.NewHabitData;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.repository.entity.HabitType;
import com.habitus.habitus.repository.entity.ScheduleType;
import com.habitus.habitus.security.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class HabitService {

    private HabitRepository repository;
    private HabitGroupRepository groupRepository;

    public HabitData getHabit(UserInfo user, Long id) {
        return toHabitData(repository.findByIdAndOwner(id, user).orElseThrow());
    }

    public void addHabit(UserInfo user, NewHabitData data) {
        var group = groupRepository.findByIdAndOwner(data.getGroupId(), user).orElseThrow();
        var habit = Habit.builder()
                .name(data.getName())
                .startDate(LocalDate.now())
                .type(HabitType.valueOf(data.getType()))
                .schedule(data.getSchedule() != null ? ScheduleType.valueOf(data.getSchedule()) : ScheduleType.EVERYDAY)
                .scheduleN(data.getScheduleN())
                .stats(HabitStats.builder()
                        .completion(0)
                        .completionCount(0)
                        .maxStreak(0)
                        .maxMiss(0)
                        .currentStreak(0)
                        .currentMiss(0)
                        .build())
                .hidden(data.isHidden())
                .position(group.getHabits().size())
                .owner(user)
                .group(group)
                .build();
        habit.getStats().setHabit(habit);
        repository.save(habit);
    }

    public void configureHabit(UserInfo user, ConfigureHabitData data) {
        var habit = repository.findByIdAndOwner(data.getHabitId(), user).orElseThrow();

        if (data.getName() != null && !data.getName().isEmpty()) habit.setName(data.getName());
        if (data.getStartDate() != null) habit.setStartDate(data.getStartDate());
        if (data.getEndDate() != null) habit.setEndDate(data.getEndDate());
        if (data.getSchedule() != null) habit.setSchedule(ScheduleType.valueOf(data.getSchedule()));
        if (data.getScheduleN() != null) habit.setScheduleN(data.getScheduleN());
        if (data.getHidden() != null) habit.setHidden(data.getHidden());
        if (data.getGroupId() != null) {
            habit.setGroup(groupRepository.findByIdAndOwner(data.getGroupId(), user).orElseThrow());
        }

        repository.save(habit);
    }

    public void deleteHabit(UserInfo user, Long id) {
        var habit = repository.findByIdAndOwner(id, user).orElseThrow();
        repository.delete(habit);
    }

    public static HabitData toHabitData(Habit habit) {
        return toHabitData(habit, null, null);
    }

    public static HabitData toHabitData(Habit habit, List<RecordData> records) {
        return toHabitData(habit, records, null);
    }

    public static HabitData toHabitData(Habit habit, List<RecordData> records, List<String> motivations) {
        return HabitData.builder()
                .id(habit.getId())
                .name(habit.getName())
                .startDate(habit.getStartDate())
                .endDate(habit.getEndDate())
                .type(habit.getType())
                .schedule(habit.getSchedule())
                .scheduleN(habit.getScheduleN())
                .hidden(habit.isHidden())
                .position(habit.getPosition())
                .motivations(motivations)
                .records(records)
                .build();
    }
}
