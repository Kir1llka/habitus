package com.habitus.habitus.api.habits;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HabitData {
    private Long id;
    private String name;
    private String type;
}
