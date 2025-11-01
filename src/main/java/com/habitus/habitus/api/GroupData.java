package com.habitus.habitus.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupData {
    private Long id;
    private String name;
    private String color;
    private List<HabitData> habits;
}
