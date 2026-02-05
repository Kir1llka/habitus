package com.habitus.habitus;

import com.habitus.habitus.repository.HabitGroupRepository;
import com.habitus.habitus.repository.HabitRepository;
import com.habitus.habitus.security.UserDetailsServiceImpl;
import com.habitus.habitus.security.UserRepository;
import com.habitus.habitus.service.DemoService;
import com.habitus.habitus.service.RecordService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected TestRestTemplate rest;
    @Autowired
    protected UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    protected HabitGroupRepository groupRepository;
    @Autowired
    protected HabitRepository habitRepository;
    @Autowired
    protected RecordService recordService;
    @Autowired
    protected DemoService demoService;

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    protected void restoreDemoData() {
        demoService.restoreDemoData();
    }

    protected void clearDb() {
        habitRepository.deleteAll();
        groupRepository.deleteAll();
    }

    protected HttpHeaders authHeaders() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", "admin");
        form.add("password", "admin");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> entity = new HttpEntity<>(form, headers);

        ResponseEntity<Void> login =
                rest.postForEntity("/login", entity, Void.class);

        headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE,
                login.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        return headers;
    }

    protected HttpEntity authEntity() {
        return new HttpEntity<>(authHeaders());
    }

    protected HttpEntity authEntity(Object body) {
        return new HttpEntity<>(body, authHeaders());
    }

    @Data
    @AllArgsConstructor
    protected static class LoginRequest {
        private String username;
        private String password;
    }
}
