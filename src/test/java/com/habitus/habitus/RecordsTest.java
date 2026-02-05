package com.habitus.habitus;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.records.data.GroupsResponse;
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

public class RecordsTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        AbstractIntegrationTest.registerProps(registry);
    }

    @Test
    void getDay_shouldSuccess() {
        URI uri = UriComponentsBuilder
                .fromPath("/api/records/day")
                .queryParam("date", LocalDate.now().toString())
                .build()
                .toUri();

        ResponseEntity<Result<GroupsResponse>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getGroupsRecords_shouldSuccess() {
        URI uri = UriComponentsBuilder
                .fromPath("/api/records")
                .queryParam("startDate", LocalDate.now().minusDays(3).toString())
                .queryParam("endDate", LocalDate.now().toString())
                .build()
                .toUri();

        ResponseEntity<Result<GroupsResponse>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    //todo остальные апишки хотя бы верхнеуровнево почекать, а так вообще норм проверить на работоспособность
}
