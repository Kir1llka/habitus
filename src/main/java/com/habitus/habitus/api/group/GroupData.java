package com.habitus.habitus.api.group;

import com.habitus.habitus.api.habits.HabitData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupData {
    private Long id;
    private String name;
    private String color;
    private boolean hidden;
    private boolean minimized;
    private List<HabitData> habits;
}
