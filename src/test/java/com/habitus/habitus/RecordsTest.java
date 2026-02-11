package com.habitus.habitus;

import com.habitus.habitus.api.Result;
import com.habitus.habitus.api.records.data.GroupsResponse;
import com.habitus.habitus.repository.entity.Habit;
import com.habitus.habitus.repository.entity.HabitGroup;
import com.habitus.habitus.repository.entity.HabitStats;
import com.habitus.habitus.repository.entity.HabitType;
import com.habitus.habitus.repository.entity.ScheduleType;
import com.habitus.habitus.security.UserInfo;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class RecordsTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        AbstractIntegrationTest.registerProps(registry);
    }

    @Test
    void getDay_shouldSuccess() {
        var response = getDay(LocalDate.now());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getDay_shouldSuccessWithNoData() {

        clearDb();

        var response = getDay(LocalDate.now());

        restoreDemoData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getDay_shouldSuccessWithHabitWithNoRecords() {

        clearDb();
        UserInfo admin = userDetailsService.getUser(1L);
        var habitName = "test1-1";
        var h = Habit.builder().name(habitName).type(HabitType.GENERAL).owner(admin).startDate(LocalDate.now())
                .schedule(ScheduleType.EVERYDAY).build();
        h.setStats(HabitStats.builder().habit(h).build());
        var gr = HabitGroup.builder().id(999L).owner(admin).habits(List.of(h)).name("test1").startDate(LocalDate.now()).build();
        h.setGroup(gr);
        groupRepository.save(gr);

        var response = getDay(LocalDate.now());

        restoreDemoData();

        assertEquals(habitName, response.getBody().getData().getGroups().get(0).getHabits().get(0).getName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private ResponseEntity<Result<GroupsResponse>> getDay(LocalDate date) {
        URI uri = UriComponentsBuilder
                .fromPath("/api/records/day")
                .queryParam("date", date.toString())
                .build()
                .toUri();

        return rest.exchange(
                uri,
                HttpMethod.GET,
                authEntity(),
                new ParameterizedTypeReference<>() {
                }
        );
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
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    //todo остальные апишки хотя бы верхнеуровнево почекать, а так вообще норм проверить на работоспособность
}
