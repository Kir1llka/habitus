package com.habitus.habitus.api.records.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PutRecordBody {
    private Long habitId;
    private LocalDate date;
    private String value;
}
