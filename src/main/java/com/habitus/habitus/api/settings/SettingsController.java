package com.habitus.habitus.api.settings;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.repository.UserSettingsRepository;
import com.habitus.habitus.security.UserDetailsInfo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users/settings")
@AllArgsConstructor
public class SettingsController {

    private UserSettingsRepository repository;

    @Operation(summary = "Получить настройки пользователя")
    @GetMapping()
    public Result<UserSettingsData> getSettings(@AuthenticationPrincipal UserDetailsInfo user) {
        var settings = user.getUser().getSettings();
        return Result.ok(UserSettingsData.builder()
                .showHidden(settings.isShowHidden())
                .displayHints(settings.isDisplayHints())
                .dashboardHint(settings.isDashboardHint())
                .tableHint(settings.isTableHint())
                .settingsHint(settings.isSettingsHint())
                .build());
    }

    @Operation(summary = "Изменить настройки пользователя")
    @PostMapping()
    public Result<Void> changeSettings(
            @AuthenticationPrincipal UserDetailsInfo user,
            @Valid @RequestBody UserSettingsRequestData data) {
        var settings = user.getUser().getSettings();

        if (data.getShowHidden() != null) settings.setShowHidden(data.getShowHidden());
        if (data.getDisplayHints() != null) settings.setDisplayHints(data.getDisplayHints());
        if (data.getDashboardHint() != null) settings.setDashboardHint(data.getDashboardHint());
        if (data.getTableHint() != null) settings.setTableHint(data.getTableHint());
        if (data.getSettingsHint() != null) settings.setSettingsHint(data.getSettingsHint());

        repository.save(settings);
        return Result.ok();
    }
}
