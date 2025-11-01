package com.habitus.habitus.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "habit_id")
    private Long habitId;

    // Используем дату без времени — если важно различать время, можно взять LocalDateTime
    @Column(name = "record_date")
    private LocalDate recordDate;
}
