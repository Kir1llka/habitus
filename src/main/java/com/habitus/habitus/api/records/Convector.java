package com.habitus.habitus.api.records;

import com.habitus.habitus.api.records.data.DayData;
import com.habitus.habitus.api.records.data.DayRecordData;
import com.habitus.habitus.api.records.data.GroupData;
import com.habitus.habitus.api.records.data.HabitData;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.RecordInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Convector {

    public static RecordData toRecordData(RecordInfo recordInfo) {
        return RecordData.builder()
                .date(recordInfo.getId().getRecordDate())
                .value(recordInfo.getPayload())
                .build();
    }

    public static HabitData toHabitData(Habit habit, LocalDate startDate, LocalDate endDate) {
        var map = habit.getRecords().stream()
                .map(Convector::toRecordData)
                .collect(Collectors.toMap(RecordData::getDate, Function.identity()));
        List<RecordData> records = Stream.iterate(
                        startDate,
                        date -> !date.isAfter(endDate),
                        date -> date.plusDays(1)
                )
                .map(date -> map.getOrDefault(date, RecordData.builder().date(date).build()))
                .collect(Collectors.toList());

        return HabitData.builder()
                .id(habit.getId())
                .name(habit.getName())
                .type(habit.getType())
                .records(records)
                .build();
    }

    public static GroupData toGroupData(HabitGroup group, LocalDate startDate, LocalDate endDate) {
        return GroupData.builder()
                .id(group.getId())
                .name(group.getName())
                .color(group.getColor())
                .habits(group.getHabits().stream()
                        .map((Habit habit) -> toHabitData(habit, startDate, endDate))
                        .toList())
                .build();
    }

    public static List<DayData> toDayDataList(List<HabitGroup> groups, LocalDate startDate, LocalDate endDate) {
        List<DayData> result = new ArrayList<>();
        var habits = groups.stream()
                .flatMap(g -> g.getHabits().stream())
                .toList();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            var d = LocalDate.ofEpochDay(date.toEpochDay());
            List<DayRecordData> recs = new ArrayList<>();
            for(Habit habit: habits) {
                var value = habit.getRecords().stream()
                        .filter(r -> r.getId().getRecordDate().equals(d))
                        .findFirst()
                        .map(RecordInfo::getPayload)
                        .orElse(null);
                recs.add(DayRecordData.builder().habitId(habit.getId()).value(value).build());
            }
            result.add(new DayData(date, recs));
        }

        return result;
    }
}
