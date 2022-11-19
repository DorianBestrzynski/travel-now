package com.zpi.accommodationservice.votes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationVote {

    @EmbeddedId
    private AccommodationVoteId id;
}
