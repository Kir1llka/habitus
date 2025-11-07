package com.habitus.habitus.api.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewGroupData {
    @NotBlank
    private String name;
    private String color;
}
