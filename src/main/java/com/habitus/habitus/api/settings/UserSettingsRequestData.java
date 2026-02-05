package com.habitus.habitus.api.settings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSettingsRequestData {
    private Boolean showHidden;
    private Boolean displayHints;
    private Boolean dashboardHint;
    private Boolean tableHint;
    private Boolean settingsHint;
}
