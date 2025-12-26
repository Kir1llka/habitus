package com.habitus.habitus.api.settings;

import lombok.Data;

@Data
public class UserSettingsRequestData {
    private Boolean showHidden;
    private Boolean displayHints;
    private Boolean dashboardHint;
    private Boolean tableHint;
    private Boolean settingsHint;
}
