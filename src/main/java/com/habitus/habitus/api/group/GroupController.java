package com.habitus.habitus.api.group;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.habits.HabitData;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Operation(summary = "Получить инфо о всех группах и привычках в них")
    @GetMapping("/all")
    public Result<List<GroupData>> getAllGroups(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails
    ) {
        return Result.ok(service.getAllGroups(userDetails.getUser()).stream()
                .map(group -> GroupData.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .color(group.getColor())
                        .hidden(group.isHidden())
                        .minimized(group.isMinimized())
                        .habits(group.getHabits().stream()
                                .map(habit -> HabitData.builder()
                                        .id(habit.getId())
                                        .name(habit.getName())
                                        .type(habit.getType())
                                        .hidden(habit.isHidden())
                                        .build())
                                .toList())
                        .build())
                .toList());
    }

    @Operation(summary = "Получить инфо о группе и её привычках")
    @GetMapping("/{id}")
    public Result<GroupData> getGroup(@PathVariable Long id) {
        var group = service.getGroup(id);
        return Result.ok(GroupData.builder()
                .id(group.getId())
                .name(group.getName())
                .color(group.getColor())
                .hidden(group.isHidden())
                .minimized(group.isMinimized())
                .habits(group.getHabits().stream()
                        .map(habit -> HabitData.builder()
                                .id(habit.getId())
                                .name(habit.getName())
                                .type(habit.getType())
                                .hidden(habit.isHidden())
                                .build())
                        .toList())
                .build());
    }

    @Operation(summary = "Добавить новую группу")
    @PostMapping()
    public Result<Void> addGroup(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @Valid @RequestBody
            NewGroupData data
    ) {
        service.addGroup(userDetails.getUser(), data);
        return Result.ok();
    }

    @Operation(summary = "Изменить параметры группы")
    @PostMapping("/configure")
    public Result<Void> configureGroup(@Valid @RequestBody ConfigureGroupData data) {
        service.configureGroup(data);
        return Result.ok();
    }

    @Operation(summary = "Удалить группу (со всеми привычками)")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGroup(@PathVariable Long id) {
        service.deleteGroup(id);
        return Result.ok();
    }
}
