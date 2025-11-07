package com.habitus.habitus.api.group;

import lombok.Data;

import java.util.List;

@Data
public class ConfigureGroupData {
    private Long groupId;
    private String name;
    private String color;
    private Boolean hidden;
    private Boolean minimized;
    private List<Long> orderedIds;
}
