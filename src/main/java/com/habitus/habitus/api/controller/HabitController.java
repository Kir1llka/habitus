package com.habitus.habitus.api.controller;

import com.habitus.habitus.api.Convector;
import com.habitus.habitus.api.GroupData;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;


    @Operation(summary = "Получить группу привычек со всеми записями в диапазоне дат")
    @GetMapping("/records")
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
        return habitService.getRecordsBetweenDates(userDetails.getUser(), startDate, endDate).stream()
                .map(Convector::toGroupData)
                .toList();
    }
}
