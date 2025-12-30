package com.habitus.habitus.api.habits;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ConfigureHabitData {
    @NotNull
    private Long habitId;
    private Long groupId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String schedule;
    private Integer scheduleN;
    private Boolean hidden;
}
