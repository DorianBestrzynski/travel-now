package com.zpi.tripgroupservice.tripgroupservice.invitation;

import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Invitation {
    @Id
    @Column(name = "invitation_id", unique = true, nullable = false)
    private String invitationId;

    @ManyToOne()
    @JoinColumn(name = "trip_group_id")
    private TripGroup tripGroup;

    public Invitation(String invitationId, TripGroup tripGroup) {
        this.invitationId = invitationId;
        this.tripGroup = tripGroup;
    }
}
