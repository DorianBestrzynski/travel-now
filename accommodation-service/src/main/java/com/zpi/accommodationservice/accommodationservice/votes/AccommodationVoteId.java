package com.zpi.accommodationservice.accommodationservice.votes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccommodationVoteId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "accommodation_id")
    private Long accommodationId;
}
