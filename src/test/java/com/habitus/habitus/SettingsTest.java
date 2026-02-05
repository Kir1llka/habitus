package com.habitus.habitus;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.records.data.GroupsResponse;
import com.habitus.habitus.api.settings.UserSettingsData;
import com.habitus.habitus.api.settings.UserSettingsRequestData;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettingsTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        AbstractIntegrationTest.registerProps(registry);
    }

    @Test
    void getSettings_shouldSuccess() {
        var response = getSettings();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private ResponseEntity<Result<UserSettingsData>> getSettings() {
        return rest.exchange(
                "/api/users/settings",
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    void changeSettings_shouldSuccessAndChangeVariable() {
        var data = UserSettingsRequestData.builder()
                .showHidden(true)
                .build();

        URI uri = UriComponentsBuilder.fromPath("/api/users/settings").build().toUri();

        ResponseEntity<Result<Void>> response = rest.exchange(
                uri,
                HttpMethod.POST,
                authEntity(data),
                new ParameterizedTypeReference<>() {
                }
        );
        var getResponse = getSettings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertNotNull(getResponse.getBody().getData());
        assertTrue(getResponse.getBody().getData().isShowHidden());
        // todo - желательно проверить изменяемость остальных полей
    }

}
