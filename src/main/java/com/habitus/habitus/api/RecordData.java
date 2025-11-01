package com.habitus.habitus.api;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RecordData {
    private LocalDate date;
    private String value;
}
