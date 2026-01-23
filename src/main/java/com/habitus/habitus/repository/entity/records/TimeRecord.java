package com.habitus.habitus.repository.entity.records;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
public class TimeRecord extends RecordInfo {
    private LocalTime payload;

    @Override
    public LocalTime getPayload() {
        return payload;
    }
}
