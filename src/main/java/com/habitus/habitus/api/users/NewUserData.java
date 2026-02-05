package com.habitus.habitus.api.users;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewUserData {
    public String name;
    public String password;
}
