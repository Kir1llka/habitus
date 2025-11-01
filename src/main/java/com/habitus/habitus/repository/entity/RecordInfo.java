package com.habitus.habitus.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.habitus.habitus.security.UserInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordInfo {

    @EmbeddedId
    private RecordId id;

    @JsonIgnore
    // Привычка
    @MapsId("habitId") // синхронизация с полем habitId из RecordId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Habit habit;

    @JsonIgnore
    // Пользователь
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserInfo user;

    // Дополнительные данные
    @Column(columnDefinition = "TEXT")
    private String payload;
}
