package com.habitus.habitus.api.records.data;

import com.habitus.habitus.api.group.GroupData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class GroupsResponse {
    private List<LocalDate> dates;
    private List<GroupData> groups;
}
