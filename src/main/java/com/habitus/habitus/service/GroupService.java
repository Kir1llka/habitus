package com.habitus.habitus.service;

import com.habitus.habitus.api.group.ConfigureGroupData;
import com.habitus.habitus.api.group.NewGroupData;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.security.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {

    private HabitGroupRepository repository;

    public List<HabitGroup> getAllGroups(UserInfo user) {
        return repository.findByOwner(user);
    }

    public HabitGroup getGroup(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public void addGroup(UserInfo user, NewGroupData data) {
        var group = HabitGroup.builder()
                .name(data.getName())
                .color(data.getColor())
                .owner(user)
                .build();
        repository.save(group);
    }

    public void configureGroup(ConfigureGroupData data) {
        var group = getGroup(data.getGroupId());

        if (!data.getName().isEmpty()) group.setName(data.getName());
        if (!data.getColor().isEmpty()) group.setColor(data.getColor());
        if (data.getHidden() != null) group.setHidden(data.getHidden());
        if (data.getMinimized() != null) group.setMinimized(data.getMinimized());

        repository.save(group);
    }

    public void deleteGroup(Long id) {
        repository.deleteById(id);
    }
}
