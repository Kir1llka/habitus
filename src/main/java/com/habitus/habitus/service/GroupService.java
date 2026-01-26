package com.habitus.habitus.service;

import com.habitus.habitus.api.group.ConfigureGroupData;
import com.habitus.habitus.api.group.GroupData;
import com.habitus.habitus.api.group.NewGroupData;
import com.habitus.habitus.api.habits.HabitData;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.security.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {

    private HabitGroupRepository repository;
    private HabitRepository habitRepository;

    public List<GroupData> getAllGroups(UserInfo user) {
        return user.getGroups().stream()
                .map(group ->
                        toGroupData(group, group.getHabits().stream()
                                .map(HabitService::toHabitData)
                                .toList())
                )
                .toList();
    }

    public GroupData getGroup(UserInfo user, Long id) {
        var group = repository.findByIdAndOwner(id, user).orElseThrow();
        return toGroupData(
                group,
                group.getHabits().stream()
                        .map(HabitService::toHabitData)
                        .toList()
        );
    }

    public void addGroup(UserInfo user, NewGroupData data) {
        var group = HabitGroup.builder()
                .name(data.getName())
                .startDate(LocalDate.now())
                .color(data.getColor())
                .owner(user)
                .position(user.getGroups().size())
                .build();
        repository.save(group);
    }

    @Transactional
    public void reorderGroups(UserInfo user, List<Long> orderedIds) {
        if (orderedIds.size() != user.getGroups().size()) throw new IllegalArgumentException("Не верная длинна массива orderedIds");
        for (int i = 0; i < orderedIds.size(); i++) {
            repository.updatePosition(orderedIds.get(i), i);
        }
    }

    @Transactional
    public void configureGroup(UserInfo user, ConfigureGroupData data) {
        var group = repository.findByIdAndOwner(data.getGroupId(), user).orElseThrow();

        if (data.getName() != null && !data.getName().isEmpty()) group.setName(data.getName());
        if (data.getStartDate() != null) group.setStartDate(data.getStartDate());
        if (data.getEndDate() != null) group.setEndDate(data.getEndDate());
        if (data.getName() != null && !data.getName().isEmpty()) group.setName(data.getName());
        if (data.getColor() != null && !data.getColor().isEmpty()) group.setColor(data.getColor());
        if (data.getHidden() != null) group.setHidden(data.getHidden());
        if (data.getMinimized() != null) group.setMinimized(data.getMinimized());
        if (data.getOrderedIds() != null) {
            if (data.getOrderedIds().size() != group.getHabits().size()) throw new IllegalArgumentException("Неверная длинна массива orderedIds");
            for (int i = 0; i < data.getOrderedIds().size(); i++) {
                habitRepository.updatePosition(data.getOrderedIds().get(i), i);
            }
        }

        repository.save(group);
    }

    public void deleteGroup(UserInfo user, Long id) {
        var group = repository.findByIdAndOwner(id, user).orElseThrow();
        repository.delete(group);
    }

    public static GroupData toGroupData(HabitGroup group, List<HabitData> habits) {
        return GroupData.builder()
                .id(group.getId())
                .name(group.getName())
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .color(group.getColor())
                .hidden(group.isHidden())
                .minimized(group.isMinimized())
                .position(group.getPosition())
                .habits(habits)
                .build();
    }
}
