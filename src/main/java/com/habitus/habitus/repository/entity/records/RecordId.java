package com.habitus.habitus.repository.entity.records;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordId implements Serializable {

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "habit_id")
    private Long habitId;
}
