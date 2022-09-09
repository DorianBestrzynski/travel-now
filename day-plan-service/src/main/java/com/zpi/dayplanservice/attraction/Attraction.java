package com.zpi.dayplanservice.attraction;

import com.zpi.dayplanservice.day_plan.DayPlan;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Attraction {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "attraction_sequence")
    @SequenceGenerator(
            name = "attraction_sequence",
            sequenceName = "attraction_sequence", allocationSize = 10)
    private Long attraction_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "opening_hours", nullable = false, length = 10)
    private LocalTime openingHours;

    @Column(name = "closing_hour", nullable = false, length = 10)
    private LocalTime closingHour;

    @Column(name = "attraction_link", nullable = false, length = 255)
    private String attractionLink;

    @ManyToMany(mappedBy = "dayAttractions")
    private Set<DayPlan> days;

    public Attraction(String name, String location, String description, LocalTime openingHours, LocalTime closingHour,
                      String attractionLink, Set<DayPlan> days) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.openingHours = openingHours;
        this.closingHour = closingHour;
        this.attractionLink = attractionLink;
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attraction that = (Attraction) o;
        return attraction_id != null && Objects.equals(attraction_id, that.attraction_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
