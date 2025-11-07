package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.security.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitGroupRepository extends JpaRepository<HabitGroup, Long> {

    List<HabitGroup> findByOwner(UserInfo owner);

    @Modifying
    @Query("UPDATE HabitGroup h SET h.position = :position WHERE h.id = :id")
    void updatePosition(@Param("id") Long id, @Param("position") int position);
}