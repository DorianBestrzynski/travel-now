package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.commons.GroupStage;
import com.zpi.tripgroupservice.tripgroupservice.invitation.Invitation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class TripGroup {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "group_sequence"
    )
    @SequenceGenerator(
            name = "group_sequence",
            sequenceName = "group_sequence", allocationSize = 10)
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

    @Column(name = "group_stage", nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupStage groupStage;

    @OneToMany(mappedBy = "tripGroup", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Invitation> invitations;

    public TripGroup(String name, Currency currency, String description, Integer votesLimit, String startLocation, GroupStage groupStage, Set<Invitation> invitations) {
        this.name = name;
        this.currency = currency;
        this.description = description;
        this.votesLimit = votesLimit;
        this.startLocation = startLocation;
        this.groupStage = groupStage;
        this.invitations = invitations;
    }
}

