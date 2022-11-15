package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.commons.GroupStage;
import com.zpi.tripgroupservice.tripgroupservice.invitation.Invitation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.zpi.tripgroupservice.tripgroupservice.commons.Utils.DEFAULT_DESCRIPTION;
import static com.zpi.tripgroupservice.tripgroupservice.commons.Utils.DEFAULT_VOTES_LIMIT;

@Entity
@Getter
@NoArgsConstructor
@Setter
@EqualsAndHashCode
public class TripGroup {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "group_sequence"
    )
    @SequenceGenerator(
            name = "group_sequence",
            sequenceName = "group_sequence", allocationSize = 1)
    @Column(unique = true, nullable = false)
    @Getter
    private Long groupId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "description", length = 120)
    private String description;

    @Column(name = "votes_limit")
    private Integer votesLimit;

    @Column(name = "start_location", nullable = false, length = 100)
    private String startLocation;

    @Column(name = "start_city", nullable = false, length = 100)
    private String startCity;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "group_stage", nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupStage groupStage;

    @OneToMany(mappedBy = "tripGroup", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Invitation> invitations = new HashSet<>();

    @Column(name = "minimal_number_of_days", nullable = false)
    private Integer minimalNumberOfDays;

    @Column(name = "minimal_number_of_participants", nullable = false)
    private Integer minimalNumberOfParticipants;

    @Getter
    @Setter
    @Column(name = "selected_accommodation_id", nullable = true)
    private Long selectedAccommodationId;

    public TripGroup(String name, Currency currency, String description, Integer votesLimit, String startLocation, String startingCity, Integer minimalNumberOfDays, Integer minimalNumberOfParticipants) {
        this.name = name;
        this.currency = currency;
        this.description = Objects.requireNonNullElse(description,DEFAULT_DESCRIPTION + name);
        this.votesLimit = Objects.requireNonNullElse(votesLimit,DEFAULT_VOTES_LIMIT);
        this.startLocation = startLocation;
        this.startCity = startingCity;
        this.groupStage = GroupStage.PLANNING_STAGE;
        this.minimalNumberOfDays = Objects.requireNonNullElse(minimalNumberOfDays, 2);
        this.minimalNumberOfParticipants = Objects.requireNonNullElse(minimalNumberOfParticipants, 2);
    }

    public void addInvitation(Invitation invitation) {
        invitations.add(invitation);
    }


}

