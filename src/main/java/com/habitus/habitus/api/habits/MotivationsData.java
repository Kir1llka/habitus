package com.habitus.habitus.api.habits;

import com.habitus.habitus.service.MotivationsService.MotivationType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MotivationsData {
    private Integer number;
    private MotivationType type;
}
