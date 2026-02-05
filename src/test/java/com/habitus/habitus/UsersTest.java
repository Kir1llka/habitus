package com.habitus.habitus;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.stats.HabitStatsData;
import com.habitus.habitus.api.users.NewUserData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class UsersTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        AbstractIntegrationTest.registerProps(registry);
    }

    @Test
    void addUser_shouldSuccess() {
        var data = NewUserData.builder()
                .name("test1")
                .password("test1")
                .build();

        URI uri = UriComponentsBuilder.fromPath("/api/users/add").build().toUri();

        ResponseEntity<Result<Void>> response = rest.exchange(
                uri,
                HttpMethod.POST,
                authEntity(data),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void hasUser_shouldSuccess() {

        URI uri = UriComponentsBuilder.fromPath("/api/users/has/{username}").buildAndExpand("admin").toUri();

        ResponseEntity<Result<Boolean>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getData());
    }
}
