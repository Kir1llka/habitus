package com.habitus.habitus.api;

import com.habitus.habitus.security.MyUserDetails;
import com.habitus.habitus.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@AllArgsConstructor
public class TestController {
    private UserRepository userRepository;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/habitus")
    public String getCurrentUser(@AuthenticationPrincipal MyUserDetails user) {

        return user.getUsername();
    }
}
