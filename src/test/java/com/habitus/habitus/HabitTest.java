package com.habitus.habitus;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.habits.HabitData;
import com.habitus.habitus.api.habits.NewHabitData;
import com.habitus.habitus.repository.entity.HabitType;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HabitTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        AbstractIntegrationTest.registerProps(registry);
    }

    @Test
    void getHabit_shouldReturnHabit() {

        ResponseEntity<Result<HabitData>> response = rest.exchange(
                "/api/habits/{id}",
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {},
                1
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().getId());
    }

    @Test
    void addHabit_shouldSuccess() {

        var data = new NewHabitData();
        data.setGroupId(1L);
        data.setName("new h");
        data.setType(HabitType.GENERAL.toString());

        ResponseEntity<Result<Void>> response = rest.exchange(
                "/api/habits",
                HttpMethod.POST,
                authEntity(data),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    //todo остальные апишки хотя бы верхнеуровнево почекать, а так вообще норм проверить на работоспособность
}
