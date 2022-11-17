package com.zpi.accommodationservice.votes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationVoteRepository extends JpaRepository<AccommodationVote, AccommodationVoteId> {
    List<AccommodationVote> findAllByIdAccommodationId(Long accommodationId);
}