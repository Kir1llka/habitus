package com.habitus.habitus.api.records.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DayRecordData {
    private Long habitId;
    private Object value;
}
