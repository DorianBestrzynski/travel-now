package com.zpi.dayplanservice.attraction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zpi.dayplanservice.day_plan.DayPlan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
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
    @Getter
    @Setter
    private Long attractionId;

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "description", nullable = true, length = 200)
    private String description;

    @Getter
    @Setter
    @JsonProperty("openingHours")
    @Column(name = "opening_hours", nullable = false, length = 255)
    private String openingHours;

    @Getter
    @Setter
    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Getter
    @Setter
    @Column(name = "attraction_link", nullable = false, length = 255)
    private String attractionLink;

    @Getter
    @Setter
    @Column(name = "photo_link", nullable = false, length = 255)
    private String photoLink;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "dayAttractions")
    @JsonIgnore
    private Set<DayPlan> days = new HashSet<>();

    @Getter
    @Setter
    @Column(name = "latitude")
    private Double latitude;

    @Getter
    @Setter
    @Column(name = "longitude")
    private Double longitude;

    public Attraction(String name, String description, String openingHours, LocalTime closingHour,
                      String address, String attractionLink, Set<DayPlan> days, Double latitude, Double longitude, String photoLink) {
        this.name = name;
        this.description = description;
        this.openingHours = openingHours;
        this.address = address;
        this.attractionLink = attractionLink;
        this.days = days;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoLink = photoLink;
    }

    public Attraction(Double destinationLatitude, Double destinationLongitude) {
        this.latitude = destinationLatitude;
        this.longitude = destinationLongitude;
    }

    public Attraction(Long attractionId, Double destinationLatitude, Double destinationLongitude, String description) {
        this.attractionId = attractionId;
        this.description = description;
        this.latitude = destinationLatitude;
        this.longitude = destinationLongitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attraction that = (Attraction) o;
        return attractionId != null && Objects.equals(attractionId, that.attractionId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Boolean addDays(List<DayPlan>  days) {
        return this.days.addAll(days);
    }

    public Boolean removeDay(DayPlan  day) {
        if(!days.isEmpty())
            return this.days.remove(day);
        else
            return false;
    }
}
