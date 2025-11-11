package com.habitus.habitus.api.records.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PutRecordBody {
    @NotNull
    private Long habitId;
    @NotNull
    private LocalDate date;
    private Object value;
}
