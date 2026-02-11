package com.habitus.habitus.api.habits;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.habitus.habitus.service.MotivationsService.MotivationType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MotivationsData {
    private Integer number;
    private MotivationType type;
}
