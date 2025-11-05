package com.habitus.habitus.api.settings;

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
@RequestMapping("api/user/settings")
@AllArgsConstructor
public class SettingsController {

    private UserSettingsRepository repository;

    @Operation(summary = "Получить настройки пользователя")
    @GetMapping()
    public UserSettingsData getSettings(@AuthenticationPrincipal UserDetailsInfo user) {
        return UserSettingsData.builder()
                .showHidden(user.getUser().getSettings().isShowHidden())
                .build();
    }

    @Operation(summary = "Изменить настройки пользователя")
    @PostMapping()
    public void changeSettings(
            @AuthenticationPrincipal UserDetailsInfo user,
            @Valid @RequestBody UserSettingsRequestData data) {
        var settings = user.getUser().getSettings();

        if (data.getShowHidden() != null) settings.setShowHidden(data.getShowHidden());

        repository.save(settings);
    }
}
