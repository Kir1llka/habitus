package com.habitus.habitus.api.records;

import com.habitus.habitus.api.records.data.GroupData;
import com.habitus.habitus.api.records.data.HabitData;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.RecordInfo;

public class Convector {

    public static RecordData toRecordData(RecordInfo recordInfo) {
        return RecordData.builder()
                .date(recordInfo.getId().getRecordDate())
                .value(recordInfo.getPayload())
                .build();
    }

    public static HabitData toHabitData(Habit habit) {
        return HabitData.builder()
                .id(habit.getId())
                .name(habit.getName())
                .type(habit.getType())
                .records(habit.getRecords().stream().map(Convector::toRecordData).toList())
                .build();
    }

    public static GroupData toGroupData(HabitGroup group) {
        return GroupData.builder()
                .id(group.getId())
                .name(group.getName())
                .color(group.getColor())
                .habits(group.getHabits().stream().map(Convector::toHabitData).toList())
                .build();
    }
}
