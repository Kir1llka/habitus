package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.records.RecordId;
import com.habitus.habitus.repository.entity.records.RecordInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDate;
import java.util.List;

@NoRepositoryBean
public interface RecordRepository<T extends RecordInfo> extends JpaRepository<T, RecordId> {

    List<T> findByHabitAndId_RecordDateBetween(Habit habit, LocalDate start, LocalDate end);
}