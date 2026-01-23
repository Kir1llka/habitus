package com.habitus.habitus.repository.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Entity
@Table(name = "habit_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Habit habit;

    private LocalDate lastUpdate;

    private Integer completion;
    private Integer completionCount;
    private Integer weekCompletion;
    private Integer maxStreak;
    private Integer maxMiss;
    private Integer currentStreak;
    private Integer currentMiss;

    private Double max;
    private Double min;
    private Double avg;
    private Double sum;

    private LocalTime maxTime;
    private LocalTime minTime;
    private Long avgTime;
    private Long sumTime;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Long> topValues;
}
