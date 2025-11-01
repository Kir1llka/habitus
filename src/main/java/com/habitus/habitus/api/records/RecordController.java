package com.habitus.habitus.api.records;

import com.habitus.habitus.api.records.data.GroupData;
import com.habitus.habitus.api.records.data.PutRecordBody;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Получить группу привычек со всеми записями в диапазоне дат")
    @GetMapping()
    public List<GroupData> getGroupsWithHabitsAndRecords(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return recordService.getRecordsBetweenDates(userDetails.getUser(), startDate, endDate).stream()
                .map(Convector::toGroupData)
                .toList();
    }

    @Operation(summary = "Добавить/обновить запись привычки за конкретную дату")
    @PutMapping()
    public void putRecord(
            @AuthenticationPrincipal
            UserDetailsInfo userDetails,

            @RequestBody PutRecordBody body
    ) {
        recordService.putRecord(userDetails.getUser(), body);
    }
}
