package com.zpi.availabilityservice.availability;

import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@NoArgsConstructor
public class Availability {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "availability_sequence")
    @SequenceGenerator(
            name = "availability_sequence",
            sequenceName = "availability_sequence", allocationSize = 10)
    @Column(name = "availability_id", nullable = false, unique = true)
    private Long availabilityID;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    public Availability(Long userId, Long groupId, LocalDate dateFrom, LocalDate dateTo) {
        this.userId = userId;
        this.groupId = groupId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Availability that = (Availability) o;
        return availabilityID != null && Objects.equals(availabilityID, that.availabilityID);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}