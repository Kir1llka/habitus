package com.habitus.habitus.api.group;

import com.habitus.habitus.api.habits.HabitData;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {

    private GroupService service;

    @GetMapping("/all")
    public List<GroupData> getAllGroups(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails
    ) {
        return service.getAllGroups(userDetails.getUser()).stream()
                .map(group -> GroupData.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .color(group.getColor())
                        .habits(group.getHabits().stream()
                                .map(habit -> HabitData.builder()
                                        .id(habit.getId())
                                        .name(habit.getName())
                                        .type(habit.getType())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @GetMapping("/{id}")
    public GroupShortData getGroup(@PathVariable Long id) {
        var group = service.getGroup(id);
        return GroupShortData.builder()
                .id(group.getId())
                .name(group.getName())
                .color(group.getColor())
                .build();
    }

    @PostMapping()
    public void addGroup(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @RequestBody
            NewGroupData data
    ) {
        service.addGroup(userDetails.getUser(), data);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        service.deleteGroup(id);
    }
}
