package com.zpi.dayplanservice.day_plan;

import com.zpi.dayplanservice.attraction.Attraction;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
public class DayPlan {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "day_sequence"
    )
    @SequenceGenerator(
            name = "day_sequence",
            sequenceName = "day_sequence", allocationSize = 10)
    @Column(name = "day_plan_id", unique = true, nullable = false)
    private Long dayPlanId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToMany
    @JoinTable(
            name = "Day_Plan_Atraction",
            joinColumns = {@JoinColumn(name = "day_plan_id")},
            inverseJoinColumns = {@JoinColumn(name = "attraction_id")}
    )
    private Set<Attraction> dayAttractions;

    public DayPlan(Long groupId, LocalDate date) {
        this.groupId = groupId;
        this.date = date;
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
}
