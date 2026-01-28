package com.habitus.habitus.api.export;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.group.GroupData;
import com.habitus.habitus.api.habits.HabitData;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.security.UserInfo;
import com.habitus.habitus.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.habitus.habitus.service.GroupService.toGroupData;
import static com.habitus.habitus.service.HabitService.toHabitData;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ExportController {
    private RecordService recordService;
    private HabitGroupRepository habitGroupRepository;
    private HabitRepository habitRepository;

    @Operation(summary = "Экспорт данных в формате json")
    @GetMapping("/export")
    public ExportData export(
            @AuthenticationPrincipal UserDetailsInfo user
    ) {
        return new ExportData(habitGroupRepository.findByOwner(user.getUser()).stream()
                .map(group ->
                        toGroupData(group, group.getHabits().stream()
                                .map(habit -> toHabitData(habit, habit.getRecords().stream()
                                        .map(RecordService::toRecordData)
                                        .toList()))
                                .toList())
                )
                .toList());
    }

    @Operation(summary = "Импорт данных в формате json")
    @PostMapping("/import")
    public Result<Void> importData(
            @AuthenticationPrincipal UserDetailsInfo userDetails,
            @RequestBody ExportData data
    ) {
        saveAll(userDetails.getUser(), data);
        return Result.ok();
    }

    @Transactional
    private void saveAll(UserInfo user, ExportData data) {

        data.groups.forEach(groupData -> {
            var group = toGroup(groupData);
            group.setOwner(user);
            habitGroupRepository.save(group);

            groupData.getHabits().forEach(habitData -> {
                var habit = toHabit(habitData);
                habit.setStats(HabitStats.builder().habit(habit).build());
                habit.setGroup(group);
                habit.setOwner(user);
                habitRepository.save(habit);

                habitData.getRecords().forEach(recordData ->
                        recordService.saveRecord(user, habit, recordData.getDate(), recordData.getValue()));
            });
        });
    }

    private HabitGroup toGroup(GroupData groupData) {
        return HabitGroup.builder()
                .name(groupData.getName())
                .startDate(groupData.getStartDate())
                .endDate(groupData.getEndDate())
                .color(groupData.getColor())
                .hidden(groupData.isHidden())
                .minimized(groupData.isMinimized())
                .position(groupData.getPosition())
                .build();
    }

    private Habit toHabit(HabitData habitData) {
        return Habit.builder()
                .name(habitData.getName())
                .startDate(habitData.getStartDate())
                .endDate(habitData.getEndDate())
                .type(habitData.getType())
                .schedule(habitData.getSchedule())
                .scheduleN(habitData.getScheduleN())
                .hidden(habitData.isHidden())
                .position(habitData.getPosition())
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportData {
        private List<GroupData> groups;
    }
}
