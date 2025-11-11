package com.habitus.habitus.repository.entity.records;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
public class TextRecord extends RecordInfo {
    private String payload;

    @Override
    public Object getPayload() {
        return payload;
    }
}
