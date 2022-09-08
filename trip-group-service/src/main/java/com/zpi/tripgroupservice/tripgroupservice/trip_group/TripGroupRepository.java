package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripGroupRepository extends JpaRepository<TripGroup, Long> {
}
