package com.habitus.habitus.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название привычки
    @Column(nullable = false)
    private String name;

    // Тип привычки (например "ежедневная", "разовая" и т.д.)
    @Column(nullable = false)
    private String type;

    @JsonIgnore
    // Группа, к которой принадлежит привычка
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private HabitGroup group;

    // Список записей (record) по этой привычке
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RecordInfo> records = new ArrayList<>();
}