package com.zpi.availabilityservice.proxies;

import com.zpi.availabilityservice.dto.AvailabilityConstraintsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/api/v1/trip-group")
public interface TripGroupProxy {

    @GetMapping("/availability-info")
    AvailabilityConstraintsDto getAvailabilityConstraints(@RequestParam Long groupId);
}
