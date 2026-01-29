package com.habitus.habitus.service;

import com.github.javafaker.Faker;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.repository.entity.HabitType;
import com.habitus.habitus.repository.entity.ScheduleType;
import com.habitus.habitus.repository.entity.UserSettings;
import com.habitus.habitus.security.Role;
import com.habitus.habitus.security.UserDetailsServiceImpl;
import com.habitus.habitus.security.UserInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DemoService {

    @Value("${loadingDays:#{null}}")
    private Integer loadingDays;

    private final UserDetailsServiceImpl userDetailsService;
    private final RecordService recordService;
    private final HabitGroupRepository habitGroupRepository;
    private final HabitRepository habitRepository;


    public void restoreDemoData() {
        var admin = userDetailsService.loadUserByUsername("admin").getUser();
        habitGroupRepository.deleteAll(habitGroupRepository.findByOwner(admin));

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
                for (LocalDate date = getStartDate(); date.isBefore(LocalDate.now()); date = date.plusDays(1)) {
                    if (faker.number().numberBetween(0, 100) < 75)
                        recordService.saveRecord(admin, habit, date, getRandomValue(type, faker));
                }
            }
        }
    }

    private LocalDate getStartDate() {
        if (loadingDays != null) {
            return LocalDate.now().minusDays(loadingDays);
        }
        return LocalDate.of(2025, 10, 1);
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
