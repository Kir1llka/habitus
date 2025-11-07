package com.habitus.habitus.service;

import com.habitus.habitus.api.habits.ConfigureHabitData;
import com.habitus.habitus.api.habits.HabitData;
import com.habitus.habitus.api.habits.NewHabitData;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HabitService {

    private HabitRepository repository;
    private HabitGroupRepository groupRepository;

    public HabitData getHabit(Long id) {
        return toHabitData(repository.findById(id).orElseThrow());
    }

    public void addHabit(NewHabitData data) {
        var group = groupRepository.findById(data.getGroupId()).orElseThrow();
        var habit = Habit.builder()
                .name(data.getName())
                .type(HabitType.valueOf(data.getType()))
                .hidden(data.isHidden())
                .position(group.getHabits().size())
                .group(group)
                .build();
        repository.save(habit);
    }

    public void configureHabit(ConfigureHabitData data) {
        var habit = repository.findById(data.getHabitId()).orElseThrow();

        if (data.getName() != null && !data.getName().isEmpty()) habit.setName(data.getName());
        if (data.getHidden() != null) habit.setHidden(data.getHidden());
        if (data.getGroupId() != null) {
            habit.setGroup(groupRepository.findById(data.getGroupId()).orElseThrow());
        }

        repository.save(habit);
    }

    public void deleteHabit(Long id) {
        repository.deleteById(id);
    }

    public static HabitData toHabitData(Habit habit) {
        return toHabitData(habit, null);
    }

    public static HabitData toHabitData(Habit habit, List<RecordData> records) {
        return HabitData.builder()
                .id(habit.getId())
                .name(habit.getName())
                .type(habit.getType())
                .hidden(habit.isHidden())
                .position(habit.getPosition())
                .records(records)
                .build();
    }
}
