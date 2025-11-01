package com.habitus.habitus.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.habitus.habitus.security.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habit_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Уникальное имя группы привычек
    @Column(nullable = false, unique = true)
    private String name;

    private String color;

    @JsonIgnore
    // Владелец группы — пользователь
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserInfo owner;

    // Список привычек в группе
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Habit> habits = new ArrayList<>();
}
