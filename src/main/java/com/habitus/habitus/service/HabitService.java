package com.habitus.habitus.service;

import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.RecordRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.RecordId;
import com.habitus.habitus.repository.entity.RecordInfo;
import com.habitus.habitus.security.Role;
import com.habitus.habitus.security.UserDetailsServiceImpl;
import com.habitus.habitus.security.UserInfo;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class HabitService {

    private UserDetailsServiceImpl userDetailsService;
    private HabitGroupRepository habitGroupRepository;
    private HabitRepository habitRepository;
    private RecordRepository recordRepository;

    public List<HabitGroup> getRecordsBetweenDates(UserInfo user, LocalDate startDate, LocalDate endDate) {
        List<HabitGroup> groups = habitGroupRepository.findByOwner(user);

        groups.stream()
                .flatMap(g -> g.getHabits().stream())
                .forEach(habit -> {
                    List<RecordInfo> records = recordRepository.findByHabitAndId_RecordDateBetween(habit, startDate, endDate);
                    habit.setRecords(records);
                });

        return groups;
    }

    @PostConstruct
    public HabitGroup createDemoData() {
        var admin = new UserInfo();
        admin.setId(1L);
        admin.setName("admin");
        admin.setPassword("admin");
        admin.setRoles(Set.of(Role.ADMIN));

        userDetailsService.addUser(admin);

        // 1️⃣ Создаем группу привычек
        HabitGroup group = new HabitGroup();
        group.setName("Мои привычки");
        group.setColor("#3498db");
        group.setOwner(admin);
        habitGroupRepository.save(group);

        // 2️⃣ Создаем 3 привычки
        List<Habit> habits = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Habit habit = new Habit();
            habit.setName("Привычка " + i);
            habit.setType("обычная");
            habit.setGroup(group);
            habitRepository.save(habit);
            habits.add(habit);

            // 3️⃣ Создаем записи для 1–5 сентября
            for (int day = 1; day <= 5; day++) {
                LocalDate date = LocalDate.of(2023, 9, day);

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

        return habitGroupRepository.save(group);
    }
}
