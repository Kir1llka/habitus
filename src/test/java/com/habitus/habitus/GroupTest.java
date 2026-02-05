package com.habitus.habitus;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.group.ConfigureGroupData;
import com.habitus.habitus.api.group.GroupData;
import com.habitus.habitus.api.group.NewGroupData;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupTest extends AbstractIntegrationTest {

    @Test
    @Order(1)
    void getGroupsAll_shouldReturnListOf3() {

        var response = getGroupsAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
    }

    private ResponseEntity<Result<List<GroupData>>> getGroupsAll() {
        return rest.exchange(
                "/api/groups/all",
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    @Order(2)
    void getGroup_shouldReturnGroup() {

        ResponseEntity<Result<GroupData>> response = rest.exchange(
                "/api/groups/{id}",
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
    @Order(3)
    void addGroup_shouldSuccess() {

        var data = new NewGroupData();
        data.setName("new g");

        ResponseEntity<Result<Void>> response = rest.exchange(
                "/api/groups",
                HttpMethod.POST,
                authEntity(data),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void configureGroup_shouldSuccess() {

        var data = ConfigureGroupData.builder() // todo - sort habits
                .groupId(4L)
                .name("group 4")
                .build();

        ResponseEntity<Result<Void>> response = rest.exchange(
                "/api/groups/configure",
                HttpMethod.POST,
                authEntity(data),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    void deleteGroup_shouldSuccess() {
        URI uri = UriComponentsBuilder
                .fromPath("/api/groups/{id}")
                .buildAndExpand(4L)
                .toUri();

        ResponseEntity<Result<Void>> response = rest.exchange(
                uri,
                HttpMethod.DELETE,
                authEntity(),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(6)
    void reorderGroups_shouldSuccess() {

        var beginIds = getAllGroupsIds();
        var reverseIds = beginIds.stream().sorted(Comparator.reverseOrder()).toList();

        var firstResponse = reorderGroups(reverseIds);
        var shouldBeReverseIds = getAllGroupsIds();
        var secondResponse = reorderGroups(beginIds);
        var shouldBeBeginIds = getAllGroupsIds();

        assertEquals(HttpStatus.OK, firstResponse.getStatusCode());
        assertEquals(HttpStatus.OK, secondResponse.getStatusCode());
        assertEquals(beginIds, shouldBeBeginIds);
        assertEquals(reverseIds, shouldBeReverseIds);
    }

    private ResponseEntity<Result<Void>> reorderGroups(List<Long> ids) {
        return rest.exchange(
                "/api/groups/reorder",
                HttpMethod.POST,
                authEntity(ids),
                new ParameterizedTypeReference<>() {}
        );
    }

    private List<Long> getAllGroupsIds() {
        return getGroupsAll().getBody().getData().stream().map(GroupData::getId).toList();
    }

}
