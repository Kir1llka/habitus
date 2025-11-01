package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.security.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitGroupRepository extends JpaRepository<HabitGroup, Long> {

    // Найти группу по имени (уникальное)
    Optional<HabitGroup> findByName(String name);

    // Найти все группы, принадлежащие пользователю
    List<HabitGroup> findByOwner(UserInfo owner);
}