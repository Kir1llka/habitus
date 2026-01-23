package com.habitus.habitus.service;

import com.github.javafaker.Faker;
import com.habitus.habitus.api.group.GroupData;
import com.habitus.habitus.api.records.data.DayData;
import com.habitus.habitus.api.records.data.DayRecordData;
import com.habitus.habitus.api.records.data.GroupsResponse;
import com.habitus.habitus.api.records.data.PutRecordBody;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.BooleanRecordRepository;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.NumberRecordRepository;
import com.habitus.habitus.repository.TextRecordRepository;
import com.habitus.habitus.repository.TimeRecordRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.repository.entity.HabitType;
import com.habitus.habitus.repository.entity.ScheduleType;
import com.habitus.habitus.repository.entity.UserSettings;
import com.habitus.habitus.repository.entity.records.BooleanRecord;
import com.habitus.habitus.repository.entity.records.NumberRecord;
import com.habitus.habitus.repository.entity.records.RecordId;
import com.habitus.habitus.repository.entity.records.RecordInfo;
import com.habitus.habitus.repository.entity.records.TextRecord;
import com.habitus.habitus.repository.entity.records.TimeRecord;
import com.habitus.habitus.security.Role;
import com.habitus.habitus.security.UserDetailsServiceImpl;
import com.habitus.habitus.security.UserInfo;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class RecordService {

    private UserDetailsServiceImpl userDetailsService;
    private StatsService statsService;
    private HabitGroupRepository habitGroupRepository;
    private HabitRepository habitRepository;
    private BooleanRecordRepository booleanRecordRepository;
    private TextRecordRepository textRecordRepository;
    private NumberRecordRepository numberRecordRepository;
    private TimeRecordRepository timeRecordRepository;

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
                .forEach(habit -> habit.setRecords(getRecords(habit, startDate, endDate)));

        return groups;
    }

    public void putRecord(UserInfo user, PutRecordBody body) {
        var habit = habitRepository.findByIdAndOwner(body.getHabitId(), user).orElseThrow();
        saveRecord(user, habit, body.getDate(), body.getValue());
    }

    private void saveRecord(UserInfo user, Habit habit, LocalDate date, Object value) {
        var id = RecordId.builder()
                .userId(user.getId())
                .habitId(habit.getId())
                .recordDate(date)
                .build();
        if (value == null) {
            //todo delete record
        }
        switch (habit.getType()) {
            case GENERAL -> {
                booleanRecordRepository.save(BooleanRecord.builder().id(id).user(user).habit(habit).payload((Boolean) value).build());
            }
            case NUMBER -> {
                numberRecordRepository.save(NumberRecord.builder().id(id).user(user).habit(habit)
                        .payload(value instanceof Double ? (Double) value : Double.valueOf((Integer) value))
                        .build());
            }
            case TEXT -> {
                textRecordRepository.save(TextRecord.builder().id(id).user(user).habit(habit).payload((String) value).build());
            }
            case TIME -> {
                timeRecordRepository.save(TimeRecord.builder().id(id).user(user).habit(habit)
                        .payload(value instanceof LocalTime ? (LocalTime) value : LocalTime.parse((String) value))
                        .build());
            }
        }
        if (date.isBefore(habit.getStartDate())) {
            habit.setStartDate(date);
            habitRepository.save(habit);
        }
        if (habit.getEldestDate() == null || date.isAfter(habit.getEldestDate())) {
            habit.setEldestDate(date);
            habitRepository.save(habit);
        }
        statsService.updateStats(habit);
    }

    private List<RecordInfo> getRecords(Habit habit, LocalDate start, LocalDate end) {
        switch (habit.getType()) {
            case GENERAL -> {
                return booleanRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
            case NUMBER -> {
                return numberRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
            case TEXT -> {
                return textRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
            case TIME -> {
                return timeRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
        }
        throw new IllegalArgumentException();
    }

    public void restoreDemoData() {
        var admin = userDetailsService.loadUserByUsername("admin").getUser();
        var groups = habitGroupRepository.findByOwner(admin);
        habitGroupRepository.deleteAll(groups);

        createDemoData();
    }

    @PostConstruct
    public void createDemoData() {
        Faker faker = new Faker(new Random(24));

        UserInfo admin = userDetailsService.getUser(1L);
        if (admin == null) {
            admin = new UserInfo();
            admin.setId(1L);
            admin.setName("admin");
            admin.setPassword("admin");
            admin.setRegistrationDate(LocalDate.now());
            admin.setRoles(Set.of(Role.ADMIN));
            admin.setSettings(UserSettings.builder().user(admin).build());

            userDetailsService.addUser(admin);
        }
        var groups = habitGroupRepository.findByOwner(admin);
        habitGroupRepository.deleteAll(groups);

        for (int j = 1; j <= 3; j++) {
            // 1️⃣ Создаем группу привычек
            HabitGroup group = new HabitGroup();
            group.setName("Мои привычки" + j);
            group.setStartDate(LocalDate.of(2025, 10, 1));
            group.setColor("#" + j * 2 + "4" + j * 2 + "8db");
            group.setPosition(j - 1);
            group.setOwner(admin);
            habitGroupRepository.save(group);

            // 2️⃣ Создаем привычки
            for (int i = 1; i <= 4 - j + 1; i++) {
                Habit habit = new Habit();
                habit.setName(j + " Привычка " + i);
                habit.setStartDate(LocalDate.of(2025, 10, 1));
                habit.setSchedule(ScheduleType.EVERYDAY);
                habit.setStats(HabitStats.builder()
                        .completion(0)
                        .completionCount(0)
                        .maxStreak(0)
                        .maxMiss(0)
                        .currentStreak(0)
                        .currentMiss(0)
                        .build());
                habit.getStats().setHabit(habit);

                var type = HabitType.GENERAL;
                if (i == 2) type = HabitType.NUMBER;
                if (i == 3) type = HabitType.TEXT;
                if (i == 4) type = HabitType.TIME;
                habit.setType(type);
                habit.setPosition(i - 1);
                habit.setGroup(group);
                habit.setOwner(admin);
                habitRepository.save(habit);

                // 3️⃣ Создаем записи
                for (LocalDate date = LocalDate.of(2025, 10, 1); date.isBefore(LocalDate.now()); date = date.plusDays(1)) {
                    if (faker.number().numberBetween(0, 100) < 75)
                        saveRecord(admin, habit, date, getRandomValue(type, faker));
                }
            }
        }
    }

    private Object getRandomValue(HabitType type, Faker faker) {
        switch (type) {
            case GENERAL -> {
                return true;
            }
            case NUMBER -> {
                return Double.valueOf(faker.number().numberBetween(1, 100));
            }
            case TEXT -> {
                return faker.book().genre();
            }
            case TIME -> {
                return LocalTime.of(0, faker.number().numberBetween(0, 59));
            }
        }
        throw new IllegalArgumentException();
    }
}
