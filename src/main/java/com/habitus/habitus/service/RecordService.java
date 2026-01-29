package com.habitus.habitus.service;

import com.habitus.habitus.api.group.GroupData;
import com.habitus.habitus.api.records.data.GroupsResponse;
import com.habitus.habitus.api.records.data.PutRecordBody;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.repository.BooleanRecordRepository;
import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.repository.NumberRecordRepository;
import com.habitus.habitus.repository.TextRecordRepository;
import com.habitus.habitus.repository.TimeRecordRepository;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.records.BooleanRecord;
import com.habitus.habitus.repository.entity.records.NumberRecord;
import com.habitus.habitus.repository.entity.records.RecordId;
import com.habitus.habitus.repository.entity.records.RecordInfo;
import com.habitus.habitus.repository.entity.records.TextRecord;
import com.habitus.habitus.repository.entity.records.TimeRecord;
import com.habitus.habitus.security.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.habitus.habitus.service.GroupService.toGroupData;
import static com.habitus.habitus.service.HabitService.toHabitData;

@Service
@AllArgsConstructor
public class RecordService {

    private StatsService statsService;
    private HabitGroupRepository habitGroupRepository;
    private HabitRepository habitRepository;
    private BooleanRecordRepository booleanRecordRepository;
    private TextRecordRepository textRecordRepository;
    private NumberRecordRepository numberRecordRepository;
    private TimeRecordRepository timeRecordRepository;

    public GroupsResponse getGroupsData(UserInfo user, LocalDate startDate, LocalDate endDate) {
        return getGroupsData(user, startDate, endDate, false);
    }

    public GroupsResponse getGroupsData(UserInfo user, LocalDate startDate, LocalDate endDate, boolean addMotivations) {
        return new GroupsResponse(
                Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1)).toList(),
                getGroupsWithRecordsInPeriod(user, startDate, endDate, addMotivations)
        );
    }

    public List<RecordData> getHabitRecords(UserInfo user, Long habitId, LocalDate startDate, LocalDate endDate) {
        var habit = habitRepository.findByIdAndOwner(habitId, user).orElseThrow();
        return getFullRecordsData(habit, startDate, endDate);
    }

    private List<RecordData> getFullRecordsData(Habit habit, LocalDate startDate, LocalDate endDate) {
        var records = getRecords(habit, startDate, endDate);
        var map = records.stream()
                .map(RecordService::toRecordData)
                .collect(Collectors.toMap(RecordData::getDate, Function.identity()));
        return Stream.iterate(
                        startDate,
                        date -> !date.isAfter(endDate),
                        date -> date.plusDays(1)
                )
                .map(date -> map.getOrDefault(date, RecordData.builder().date(date).build()))
                .toList();
    }

    public static RecordData toRecordData(RecordInfo recordInfo) {
        return RecordData.builder()
                .date(recordInfo.getId().getRecordDate())
                .value(recordInfo.getPayload())
                .build();
    }

    private List<GroupData> getGroupsWithRecordsInPeriod(UserInfo user, LocalDate startDate, LocalDate endDate, boolean addMotivations) {
        var showHidden = user.getSettings().isShowHidden();
        return habitGroupRepository.findAllForUser(user, showHidden).stream()
                .map(g -> {
                    var habits = habitRepository.findAllForGroup(g, showHidden).stream()
                            .map(h -> {
                                var recordsData = getFullRecordsData(h, startDate, endDate);
                                return toHabitData(
                                        h,
                                        recordsData,
                                        addMotivations ? statsService.getMotivations(h, recordsData.get(0).getValue()) : null
                                );
                            })
                            .toList();
                    return toGroupData(g, habits);
                })
                .toList();
    }

    public void putRecord(UserInfo user, PutRecordBody body) {
        var habit = habitRepository.findByIdAndOwner(body.getHabitId(), user).orElseThrow();
        saveRecord(user, habit, body.getDate(), body.getValue());
    }

    public void saveRecord(UserInfo user, Habit habit, LocalDate date, Object value) {
        var id = RecordId.builder()
                .userId(user.getId())
                .habitId(habit.getId())
                .recordDate(date)
                .build();
        if (value == null) {
            //todo delete record
        }
        switch (habit.getType()) {
            case GENERAL -> {
                booleanRecordRepository.save(BooleanRecord.builder().id(id).user(user).habit(habit).payload((Boolean) value).build());
            }
            case NUMBER -> {
                numberRecordRepository.save(NumberRecord.builder().id(id).user(user).habit(habit)
                        .payload(value instanceof Double ? (Double) value : Double.valueOf((Integer) value))
                        .build());
            }
            case TEXT -> {
                textRecordRepository.save(TextRecord.builder().id(id).user(user).habit(habit).payload((String) value).build());
            }
            case TIME -> {
                timeRecordRepository.save(TimeRecord.builder().id(id).user(user).habit(habit)
                        .payload(value instanceof LocalTime ? (LocalTime) value : LocalTime.parse((String) value))
                        .build());
            }
        }
        if (date.isBefore(habit.getStartDate())) {
            habit.setStartDate(date);
            habitRepository.save(habit);
        }
        if (habit.getEldestDate() == null || date.isAfter(habit.getEldestDate())) {
            habit.setEldestDate(date);
            habitRepository.save(habit);
        }
        statsService.updateStats(habit);
    }

    private List<RecordInfo> getRecords(Habit habit, LocalDate start, LocalDate end) {
        switch (habit.getType()) {
            case GENERAL -> {
                return booleanRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
            case NUMBER -> {
                return numberRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
            case TEXT -> {
                return textRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
            case TIME -> {
                return timeRecordRepository.findByHabitAndId_RecordDateBetween(habit, start, end).stream()
                        .map(r -> (RecordInfo) r).toList();
            }
        }
        throw new IllegalArgumentException();
    }
}
