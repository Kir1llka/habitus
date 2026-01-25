package com.habitus.habitus.api.records;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.records.data.GroupsResponse;
import com.habitus.habitus.api.records.data.PutRecordBody;
import com.habitus.habitus.api.records.data.RecordData;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@AllArgsConstructor
public class RecordController {

    private RecordService recordService;

    @Operation(summary = "Получить все группы привычек со всеми записями в диапазоне дат")
    @GetMapping()
    public Result<GroupsResponse> getGroups(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return Result.ok(recordService.getGroupsData(userDetails.getUser(), startDate, endDate));
    }

    @Operation(summary = "Получить записи привычки по айди в диапазоне дат")
    @GetMapping("/habit")
    public Result<List<RecordData>> getHabitRecords(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @RequestParam("habitId")
            Long habitId,

            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return Result.ok(recordService.getHabitRecords(userDetails.getUser(), habitId, startDate, endDate));
    }

    @Operation(summary = "Получить все группы привычек со всеми записями за определенный день")
    @GetMapping("day")
    public Result<GroupsResponse> getDay(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return Result.ok(recordService.getGroupsData(userDetails.getUser(), date, date));
    }

    @Operation(summary = "Добавить/обновить запись привычки за конкретную дату")
    @PutMapping()
    public Result<Void> putRecord(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @Valid @RequestBody PutRecordBody body
    ) {
        recordService.putRecord(userDetails.getUser(), body);
        return Result.ok();
    }
}
