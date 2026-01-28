package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.security.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitGroupRepository extends JpaRepository<HabitGroup, Long> {

    Optional<HabitGroup> findByIdAndOwner(Long id, UserInfo owner);

    List<HabitGroup> findByOwner(UserInfo owner);
    List<HabitGroup> findByOwnerOrderByPosition(UserInfo owner);

    @Query("""
                select g
                from HabitGroup g
                where g.owner = :owner
                  and (:showHidden = true or g.hidden = false)
                order by g.position
            """)
    List<HabitGroup> findAllForUser(
            UserInfo owner,
            boolean showHidden
    );

    @Modifying
    @Query("UPDATE HabitGroup h SET h.position = :position WHERE h.id = :id")
    void updatePosition(@Param("id") Long id, @Param("position") int position);
}