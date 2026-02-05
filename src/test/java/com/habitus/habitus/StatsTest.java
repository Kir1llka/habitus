package com.habitus.habitus;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.records.data.GroupsResponse;
import com.habitus.habitus.api.settings.UserSettingsData;
import com.habitus.habitus.api.settings.UserSettingsRequestData;
import com.habitus.habitus.api.stats.HabitStatsData;
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
public class StatsTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        AbstractIntegrationTest.registerProps(registry);
    }

    @Test
    void getStats_shouldSuccess() {
        URI uri = UriComponentsBuilder
                .fromPath("/api/habits/{id}/stats")
                .buildAndExpand(1L)
                .toUri();

        ResponseEntity<Result<HabitStatsData>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getStatsPeriod_shouldSuccess() {
        URI uri = UriComponentsBuilder
                .fromPath("/api/habits/{id}/stats/period")
                .queryParam("startDate", LocalDate.now().minusDays(3).toString())
                .queryParam("endDate", LocalDate.now().toString())
                .buildAndExpand(1L)
                .toUri();

        ResponseEntity<Result<HabitStatsData>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    //todo Мб стоит тут проверить конкретные переменные. Или лучше делать это в юнит тестах.
}
