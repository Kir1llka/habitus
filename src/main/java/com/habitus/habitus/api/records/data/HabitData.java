package com.habitus.habitus.api.records.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HabitData {
    private Long id;
    private String name;
    private String type;
    private List<RecordData> records;
}
