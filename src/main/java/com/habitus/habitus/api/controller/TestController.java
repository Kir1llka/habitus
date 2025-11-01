package com.habitus.habitus.api.controller;

import com.habitus.habitus.api.Convector;
import com.habitus.habitus.api.GroupData;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.security.UserRepository;
import com.habitus.habitus.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/test")
@AllArgsConstructor
public class TestController {
    private UserRepository userRepository;
    private HabitService habitService;

    @Operation(summary = "Просто пинг сервера")
    @GetMapping()
    public String test() {
        return "test";
    }

    @Operation(summary = "Проверка авторизованного пользователя")
    @GetMapping("/user")
    public String getCurrentUser(@AuthenticationPrincipal UserDetailsInfo user) {
        return user.getUsername();
    }

    @Operation(summary = "Аналог обычной апи, но без авторизации")
    @GetMapping("/habits/records")
    public List<GroupData> getRecords(
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        var user = userRepository.findById(1L).orElseThrow();
        return habitService.getRecordsBetweenDates(user, startDate, endDate).stream()
                .map(Convector::toGroupData)
                .toList();
    }
}
