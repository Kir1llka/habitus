package com.habitus.habitus.api.settings;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.repository.UserSettingsRepository;
import com.habitus.habitus.repository.entity.UserSettings;
import com.habitus.habitus.security.Role;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.security.UserDetailsServiceImpl;
import com.habitus.habitus.security.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class SettingsController {

    private UserSettingsRepository repository;
    private UserDetailsServiceImpl userDetailsService;

    @Operation(summary = "Добавление нового пользователя")
    @PostMapping("/add")
    public Result<Void> addUser(@RequestBody NewUserData data) {
        UserInfo user = new UserInfo();
        user.setName(data.getName());
        user.setPassword(data.getPassword());
        user.setRegistrationDate(LocalDate.now());
        user.setRoles(Set.of(Role.USER));
        user.setSettings(UserSettings.builder()
                .showHidden(false)
                .displayHints(true)
                .dashboardHint(true)
                .tableHint(true)
                .settingsHint(true)
                .user(user)
                .build());

        userDetailsService.addUser(user);

        return Result.ok();
    }

    @GetMapping("/has/{username}")
    public Result<Boolean> hasUser(@PathVariable String username) {
        try {
            userDetailsService.loadUserByUsername(username);
            return Result.ok(true);
        } catch (UsernameNotFoundException e) {
            return Result.ok(false);
        }
    }

    @Operation(summary = "Получить настройки пользователя")
    @GetMapping("/settings")
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
    @PostMapping("/settings")
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
