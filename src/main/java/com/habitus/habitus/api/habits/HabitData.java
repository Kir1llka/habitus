package com.habitus.habitus.api.habits;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.entity.HabitType;
import com.habitus.habitus.repository.entity.ScheduleType;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.SqlReturnType;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HabitData {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private HabitType type;
    private ScheduleType schedule;
    private Integer scheduleN;
    private boolean hidden;
    private int position;
    private List<MotivationsData> motivations;
    private List<RecordData> records;
}
