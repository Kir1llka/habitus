package com.habitus.habitus.repository.entity;

import com.habitus.habitus.security.UserInfo;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordInfo {

    @EmbeddedId
    private RecordId id;

    @MapsId("habitId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Habit habit;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserInfo user;

    private String payload;
}
