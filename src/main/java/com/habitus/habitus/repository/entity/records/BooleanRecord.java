package com.habitus.habitus.repository.entity.records;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
public class BooleanRecord extends RecordInfo {
    private boolean payload;

    @Override
    public Object getPayload() {
        return payload;
    }
}
