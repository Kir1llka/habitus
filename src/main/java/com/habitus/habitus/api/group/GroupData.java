package com.habitus.habitus.api.group;

import com.habitus.habitus.api.habits.HabitData;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GroupData {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
    private boolean hidden;
    private boolean minimized;
    private int position;
    private List<HabitData> habits;
}
