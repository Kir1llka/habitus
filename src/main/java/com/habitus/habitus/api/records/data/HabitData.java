package com.habitus.habitus.api.records.data;

import com.habitus.habitus.repository.entity.HabitType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HabitData {
    private Long id;
    private String name;
    private HabitType type;
    private boolean hidden;
    private List<RecordData> records;
}
