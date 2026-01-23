package com.habitus.habitus.api.stats;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HabitStatsData {
    private Integer completion;
    private Integer completionCount;
    private Integer weekCompletion;
    private Integer maxStreak;
    private Integer maxMiss;
    private Integer currentStreak;
    private Integer currentMiss;

    private NumData numData;
    private TimeData timeData;
    private TextData textData;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NumData {
        private Double max;
        private Double min;
        private Double avg;
        private Double sum;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TimeData {
        private LocalTime max;
        private LocalTime min;
        private Long avg;
        private Long sum;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TextData {
        private Map<String, Long> topValues;
    }
}
