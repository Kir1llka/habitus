package com.habitus.habitus.api.records.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DayData {
    private LocalDate date;
    private List<DayRecordData> records;
}
