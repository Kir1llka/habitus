package com.habitus.habitus.api.habits;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewHabitData {
    @NotNull
    private Long groupId;
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    private String schedule;
    private Integer scheduleN;
    private boolean hidden;
}
