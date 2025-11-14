package com.habitus.habitus.api;

import com.habitus.habitus.security.Role;
import com.habitus.habitus.security.UserDetailsInfo;
import com.habitus.habitus.security.UserRepository;
import com.habitus.habitus.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/test")
@AllArgsConstructor
public class TestController {
    private UserRepository userRepository;
    private RecordService recordService;

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

    @Operation(summary = "Восстановить тестовые данные")
    @GetMapping("/restore")
    public void restore(@AuthenticationPrincipal UserDetailsInfo user) {

        if (user.getUser().getRoles().contains(Role.ADMIN)) {
            recordService.restoreDemoData();
        }

    }
}
