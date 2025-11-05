package com.habitus.habitus.api.habits;

import com.habitus.habitus.repository.entity.HabitType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HabitData {
    private Long id;
    private String name;
    private HabitType type;
    private boolean hidden;
}
