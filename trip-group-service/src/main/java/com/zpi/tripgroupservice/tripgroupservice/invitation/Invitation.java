package com.zpi.tripgroupservice.tripgroupservice.invitation;

import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroup;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Invitation {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "group_sequence"
    )
    @SequenceGenerator(
            name = "group_sequence",
            sequenceName = "group_sequence", allocationSize = 10)
    @Column(name = "invitation_id", unique = true, nullable = false)
    private Long invitationId;

    @ManyToOne()
    @JoinColumn(name = "trip_group_id")
    private TripGroup tripGroup;

    public Invitation(TripGroup tripGroup) {
        this.tripGroup = tripGroup;
    }
}
