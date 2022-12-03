package com.zpi.accommodationservice.votes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationVoteRepository extends JpaRepository<AccommodationVote, AccommodationVoteId> {
    List<AccommodationVote> findAllByIdAccommodationId(Long accommodationId);

    @Query("SELECT v FROM AccommodationVote v WHERE v.id.accommodationId IN :accommodationIds")
    List<AccommodationVote> findAllByAccommodationsId(List<Long> accommodationIds);

    @Query("SELECT v FROM AccommodationVote v JOIN Accommodation a ON v.id.accommodationId=a.accommodationId WHERE v.id.userId=?1 AND a.groupId=?2 ")
    List<AccommodationVote> findAllByUserIdAndGroupId(Long userId, Long groupId);
}
