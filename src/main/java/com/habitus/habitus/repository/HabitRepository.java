package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    // Найти все привычки в группе
    List<Habit> findByGroup(HabitGroup group);

    // Найти привычку по имени
    Habit findByName(String name);
}