package com.habitus.habitus.api.habits;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfigureHabitData {
    @NotNull
    private Long habitId;
    private Long groupId;
    private String name;
    private Boolean hidden;
}
