package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByGroup(HabitGroup group);

    @Modifying
    @Query("UPDATE Habit h SET h.position = :position WHERE h.id = :id")
    void updatePosition(@Param("id") Long id, @Param("position") int position);
}