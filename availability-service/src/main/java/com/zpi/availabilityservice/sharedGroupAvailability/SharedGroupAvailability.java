package com.zpi.availabilityservice.sharedGroupAvailability;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private Integer numberOfDays;
    @Column(name = "is_created_manually")
    private Boolean isCreatedManually;

//    public SharedGroupAvailability(Long groupId, List<Long> usersList, LocalDate dateFrom, LocalDate dateTo, Integer numberOfDays) {
//        this.groupId = groupId;
//        this.dateFrom = dateFrom;
//        this.dateTo = dateTo;
//        this.usersList = usersList;
//        this.numberOfDays = numberOfDays;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedGroupAvailability that = (SharedGroupAvailability) o;
        return groupId.equals(that.groupId) && Objects.equals(usersList, that.usersList) && dateFrom.equals(that.dateFrom) && dateTo.equals(that.dateTo) && numberOfDays.equals(that.numberOfDays) && isCreatedManually.equals(that.isCreatedManually);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, usersList, dateFrom, dateTo, numberOfDays, isCreatedManually);
    }

    public SharedGroupAvailability(Long groupId, List<Long> usersList, LocalDate dateFrom, LocalDate dateTo, Integer numberOfDays, Boolean isCreatedManually) {
        this.groupId = groupId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.usersList = usersList;
        this.numberOfDays = numberOfDays;
        this.isCreatedManually = isCreatedManually;
    }

    public int getNumberOfUsers(){
        return usersList.size();
    }



}
