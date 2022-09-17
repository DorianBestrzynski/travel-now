package com.zpi.accommodationservice.accommodationservice.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation,Long> {

    Optional<List<Accommodation>> findAllByGroupId(Long groupId);
}
