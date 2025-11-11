package com.habitus.habitus.service;

import com.habitus.habitus.api.group.GroupData;
import com.habitus.habitus.api.records.data.DayData;
import com.habitus.habitus.api.records.data.DayRecordData;
import com.habitus.habitus.api.records.data.GroupsResponse;
import com.habitus.habitus.api.records.data.PutRecordBody;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.RecordRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.HabitType;
import com.habitus.habitus.repository.entity.RecordId;
import com.habitus.habitus.repository.entity.RecordInfo;
import com.habitus.habitus.repository.entity.UserSettings;
import com.habitus.habitus.security.Role;
import com.habitus.habitus.security.UserDetailsServiceImpl;
import com.habitus.habitus.security.UserInfo;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class RecordService {

    private UserDetailsServiceImpl userDetailsService;
    private HabitGroupRepository habitGroupRepository;
    private HabitRepository habitRepository;
    private RecordRepository recordRepository;

    public GroupsResponse getGroupsData(UserInfo user, LocalDate startDate, LocalDate endDate) {
        return new GroupsResponse(
                Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1)).toList(),
                getRecordsBetweenDates(user, startDate, endDate).stream()
                        .map(g -> getGroupData(g, startDate, endDate))
                        .toList()
        );
    }

    private GroupData getGroupData(HabitGroup group, LocalDate startDate, LocalDate endDate) {
        return GroupService.toGroupData(
                group,
                group.getHabits().stream()
                        .map(h -> HabitService.toHabitData(h, getFullRecordsData(h, startDate, endDate)))
                        .toList()
        );
    }

    private static List<RecordData> getFullRecordsData(Habit habit, LocalDate startDate, LocalDate endDate) {
        var map = habit.getRecords().stream()
                .map(RecordService::toRecordData)
                .collect(Collectors.toMap(RecordData::getDate, Function.identity()));
        return Stream.iterate(
                        startDate,
                        date -> !date.isAfter(endDate),
                        date -> date.plusDays(1)
                )
                .map(date -> map.getOrDefault(date, RecordData.builder().date(date).build()))
                .toList();
    }

    private static RecordData toRecordData(RecordInfo recordInfo) {
        return RecordData.builder()
                .date(recordInfo.getId().getRecordDate())
                .value(recordInfo.getPayload())
                .build();
    }

    public List<DayData> getDaysData(UserInfo user, LocalDate startDate, LocalDate endDate) {
        var groups = getRecordsBetweenDates(user, startDate, endDate);

        List<DayData> result = new ArrayList<>();
        var habits = groups.stream()
                .flatMap(g -> g.getHabits().stream())
                .toList();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            var d = LocalDate.ofEpochDay(date.toEpochDay());
            List<DayRecordData> recs = new ArrayList<>();
            for (Habit habit : habits) {
                var value = habit.getRecords().stream()
                        .filter(r -> r.getId().getRecordDate().equals(d))
                        .findFirst()
                        .map(RecordInfo::getPayload)
                        .orElse(null);
                recs.add(DayRecordData.builder().habitId(habit.getId()).value(value).build());
            }
            result.add(new DayData(date, recs));
        }

        return result;
    }

    private List<HabitGroup> getRecordsBetweenDates(UserInfo user, LocalDate startDate, LocalDate endDate) {
        var showHidden = user.getSettings().isShowHidden();
        List<HabitGroup> groups = habitGroupRepository.findByOwner(user)
                .stream()
                .filter(g -> showHidden || !g.isHidden())
                .sorted(Comparator.comparing(HabitGroup::getPosition))
                .toList();

        groups.forEach(g -> g.setHabits(g.getHabits().stream()
                .filter(h -> showHidden || !h.isHidden())
                .sorted(Comparator.comparing(Habit::getPosition))
                .toList()));

        groups.stream()
                .flatMap(g -> g.getHabits().stream())
                .forEach(habit -> {
                    List<RecordInfo> records = recordRepository.findByHabitAndId_RecordDateBetween(habit, startDate, endDate);
                    habit.setRecords(records);
                });

        return groups;
    }

    public void putRecord(UserInfo user, PutRecordBody body) {
        var habit = habitRepository.findById(body.getHabitId()).orElseThrow();
        var id = RecordId.builder()
                .userId(user.getId())
                .habitId(habit.getId())
                .recordDate(body.getDate())
                .build();

        var record = RecordInfo.builder()
                .id(id)
                .user(user)
                .habit(habit)
                .payload(body.getValue())
                .build();

        recordRepository.save(record);
    }

    @PostConstruct
    public void createDemoData() {
        var admin = new UserInfo();
        admin.setId(1L);
        admin.setName("admin");
        admin.setPassword("admin");
        admin.setRoles(Set.of(Role.ADMIN));
        admin.setSettings(UserSettings.builder().user(admin).build());

        userDetailsService.addUser(admin);

        for (int j = 1; j <= 3; j++) {
            // 1️⃣ Создаем группу привычек
            HabitGroup group = new HabitGroup();
            group.setName("Мои привычки" + j);
            group.setColor("#" + j * 2 + "4" + j * 2 + "8db");
            group.setPosition(j - 1);
            group.setOwner(admin);
            habitGroupRepository.save(group);

            // 2️⃣ Создаем 3 привычки
            List<Habit> habits = new ArrayList<>();
            for (int i = 1; i <= 3 - j + 1; i++) {
                Habit habit = new Habit();
                habit.setName(j + " Привычка " + i);
                habit.setType(HabitType.GENERAL);
                habit.setPosition(i - 1);
                habit.setGroup(group);
                habitRepository.save(habit);
                habits.add(habit);

                // 3️⃣ Создаем записи для 1–5 сентября
                for (LocalDate date = LocalDate.of(2025, 10, 1); date.isBefore(LocalDate.now()); date = date.plusDays(1)) {

                    RecordId id = new RecordId();
                    id.setUserId(admin.getId());
                    id.setHabitId(habit.getId());
                    id.setRecordDate(date);

                    RecordInfo record = new RecordInfo();
                    record.setId(id);
                    record.setUser(admin);
                    record.setHabit(habit);
                    record.setPayload("DONE");

                    recordRepository.save(record);
                }
            }
            // 4️⃣ Устанавливаем привычки в группу
            group.setHabits(habits);
            habitGroupRepository.save(group);
        }

    }
}
