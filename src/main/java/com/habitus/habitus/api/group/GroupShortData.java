package com.habitus.habitus.api.group;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupShortData {
    private Long id;
    private String name;
    private String color;
}
