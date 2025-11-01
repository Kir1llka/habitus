package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.RecordId;
import com.habitus.habitus.repository.entity.RecordInfo;
import com.habitus.habitus.security.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<RecordInfo, RecordId> {

    // Найти все записи для определенной привычки
    List<RecordInfo> findByHabit(Habit habit);

    // Найти все записи для пользователя
    List<RecordInfo> findByUser(UserInfo user);

    // Найти запись по пользователю, привычке и дате
    Optional<RecordInfo> findByUserAndHabitAndId_RecordDate(UserInfo user, Habit habit, LocalDate recordDate);

    List<RecordInfo> findByHabitAndId_RecordDateBetween(Habit habit, LocalDate startDate, LocalDate endDate);
}