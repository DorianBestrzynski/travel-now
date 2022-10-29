package com.zpi.availabilityservice.sharedGroupAvailability;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SharedGroupAvailability {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "shared_group_availability_sequence")
    @SequenceGenerator(
            name = "shared_group_availability_sequence",
            sequenceName = "shared_group_availability_sequence", allocationSize = 10)
    @Column(name = "availability_id", nullable = false, unique = true)
    private Long sharedGroupAvailabilityId;
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    @Column(name = "users_list")
    @ElementCollection
    private List<Long> usersList = new ArrayList<>();
    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;
    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;
    @Column(name ="number_of_days" )
    private Long numberOfDays;

    public SharedGroupAvailability(Long groupId, List<Long> usersList, LocalDate dateFrom, LocalDate dateTo) {
        this.groupId = groupId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.usersList = usersList;
    }

    public Long getNumberOfDays(){
        return ChronoUnit.DAYS.between(dateFrom, dateTo);
    }


}
