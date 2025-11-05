package com.habitus.habitus.api.habits;

import lombok.Data;

@Data
public class NewHabitData {
    private Long groupId;
    private String name;
    private String type;
    private boolean hidden;
}
