package com.habitus.habitus.api.group;

import com.habitus.habitus.api.Result;
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
        return Result.ok(service.getAllGroups(userDetails.getUser()));
    }

    @Operation(summary = "Получить инфо о группе и её привычках")
    @GetMapping("/{id}")
    public Result<GroupData> getGroup(@PathVariable Long id) {
        return Result.ok(service.getGroup(id));
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

    @Operation(summary = "Изменить параметры группы")
    @PostMapping("/reorder")
    public Result<Void> reorderGroups(
            @AuthenticationPrincipal UserDetailsInfo userDetails,
            @RequestBody List<Long> orderedIds
    ) {
        service.reorderGroups(userDetails.getUser(), orderedIds);
        return Result.ok();
    }

    @Operation(summary = "Удалить группу (со всеми привычками)")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGroup(@PathVariable Long id) {
        service.deleteGroup(id);
        return Result.ok();
    }
}
