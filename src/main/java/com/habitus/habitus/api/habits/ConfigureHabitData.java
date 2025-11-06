package com.habitus.habitus.api.habits;

import lombok.Data;

@Data
public class ConfigureHabitData {
    private Long habitId;
    private Long groupId;
    private String name;
    private Boolean hidden;
}
