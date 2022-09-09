package com.zpi.accommodationservice.accommodationservice.votes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationVoteRepository extends JpaRepository<AccommodationVote, AccommodationVoteId> {
}
