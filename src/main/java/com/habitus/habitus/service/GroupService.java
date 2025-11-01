package com.habitus.habitus.service;

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

    public void deleteGroup(Long id) {
        repository.deleteById(id);
    }
}
