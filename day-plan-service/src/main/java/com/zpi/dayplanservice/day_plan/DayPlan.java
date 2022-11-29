package com.zpi.dayplanservice.day_plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zpi.dayplanservice.attraction.Attraction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class DayPlan {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "day_sequence"
    )
    @SequenceGenerator(
            name = "day_sequence",
            sequenceName = "day_sequence", allocationSize = 1)
    @Column(name = "day_plan_id", unique = true, nullable = false)
    private Long dayPlanId;

    @Column(name = "group_id", nullable = false)
    @JsonProperty("groupId")
    private Long groupId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "icon_type", nullable = true, length = 10)
    private Integer iconType;

    @Getter
    @ManyToMany
    @JoinTable(
            name = "Day_Plan_Atraction",
            joinColumns = {@JoinColumn(name = "day_plan_id")},
            inverseJoinColumns = {@JoinColumn(name = "attraction_id")}
    )
    private Set<Attraction> dayAttractions;

    @Getter
    @Setter
    @Column(name = "day_plan_starting_point_id", nullable = true, length = 10)
    private Long dayPlanStartingPointId;

    public DayPlan(Long groupId, LocalDate date, String name) {
        this.groupId = groupId;
        this.date = date;
        this.name = name;
        this.dayAttractions = new HashSet<>();
    }

    public DayPlan(Long groupId, LocalDate date, String name, Integer iconType) {
        this.groupId = groupId;
        this.date = date;
        this.name = name;
        this.iconType = iconType == null ? 0 : iconType;
        this.dayAttractions = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DayPlan dayPlan = (DayPlan) o;
        return dayPlanId != null && Objects.equals(dayPlanId, dayPlan.dayPlanId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean addAttraction(Attraction attraction) {
        return this.dayAttractions.add(attraction);
    }

    public Attraction deleteAttraction(Long attractionId) {
        var attractionToDelete = this.dayAttractions.stream()
                .filter(attraction -> attraction.getAttractionId().equals(attractionId))
                .findFirst()
                .orElse(null);
        if (attractionToDelete != null) {
            this.dayAttractions.remove(attractionToDelete);
            return attractionToDelete;
        }
        else {
            throw new IllegalArgumentException("Attraction with id " + attractionId + " not found");
        }
    }
}
