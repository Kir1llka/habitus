package com.habitus.habitus.service;

import com.habitus.habitus.api.habits.ConfigureHabitData;
import com.habitus.habitus.api.habits.NewHabitData;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HabitService {

    private HabitRepository repository;
    private HabitGroupRepository groupRepository;

    public Habit getHabit(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public void addHabit(NewHabitData data) {
        var group = groupRepository.findById(data.getGroupId()).orElseThrow();
        var habit = Habit.builder()
                .name(data.getName())
                .type(HabitType.valueOf(data.getType()))
                .hidden(data.isHidden())
                .group(group)
                .build();
        repository.save(habit);
    }

    public void configureHabit(ConfigureHabitData data) {
        var habit = getHabit(data.getHabitId());

        if (!data.getName().isEmpty()) habit.setName(data.getName());
        if (data.getHidden() != null) habit.setHidden(data.getHidden());

        repository.save(habit);
    }

    public void deleteHabit(Long id) {
        repository.deleteById(id);
    }
}
