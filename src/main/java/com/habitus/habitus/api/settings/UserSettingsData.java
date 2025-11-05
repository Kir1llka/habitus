package com.habitus.habitus.api.settings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSettingsData {
    private boolean showHidden;
}
