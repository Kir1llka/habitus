package com.habitus.habitus.api.group;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ConfigureGroupData {
    @NotNull
    private Long groupId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
    private Boolean hidden;
    private Boolean minimized;
    private List<Long> orderedIds;
}
