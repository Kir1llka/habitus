package com.habitus.habitus.api.users;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.repository.entity.UserSettings;
import com.habitus.habitus.security.Role;
import com.habitus.habitus.security.UserDetailsServiceImpl;
import com.habitus.habitus.security.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
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
public class UsersController {
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
}
