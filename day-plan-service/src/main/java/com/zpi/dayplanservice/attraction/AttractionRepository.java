package com.zpi.dayplanservice.attraction;

import com.google.maps.model.LatLng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    @Query("SELECT new com.google.maps.model.LatLng(a.latitude, a.longitude) FROM Attraction a JOIN a.days dp WHERE dp.dayPlanId=?1")
    List<LatLng> findAttractionsCoordinatesForGivenDay(Long dayPlanId);

    @Query("SELECT a FROM Attraction a JOIN a.days dp WHERE dp.dayPlanId=?1")
    ArrayList<Attraction> findAttractionsByDayPlanId(Long dayPlanId);
}
