package com.zpi.accommodationservice.accommodation;

import com.zpi.accommodationservice.dto.AccommodationInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation,Long> {

    Optional<List<Accommodation>> findAllByGroupId(Long groupId);

    @Query("SELECT new com.zpi.accommodationservice.dto.AccommodationInfoDto(a.streetAddress, a.city, a.latitude, a.longitude, a.groupId, a.creator_id) FROM Accommodation a WHERE a.accommodationId=?1")
    AccommodationInfoDto getAccommodationInfoDto(Long accommodationInfo);

    boolean existsByAccommodationIdAndGroupId(Long accommodationId, Long groupId);
}
