package com.habitus.habitus.api.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HabitStatsData {
    private Integer completion;
    private Integer completionCount;
    private Integer weekCompletion;
    private Integer maxStreak;
    private Integer maxMiss;
    private Integer currentStreak;
    private Integer currentMiss;
}
