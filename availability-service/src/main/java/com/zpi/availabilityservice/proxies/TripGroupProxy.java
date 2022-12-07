package com.zpi.availabilityservice.proxies;

import com.zpi.availabilityservice.dto.AvailabilityConstraintsDto;
import com.zpi.availabilityservice.dto.SelectedAvailabilityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/")
public interface TripGroupProxy {

    @GetMapping("api/v1/trip-group/availability-info")
    AvailabilityConstraintsDto getAvailabilityConstraints(@RequestHeader("innerCommunication") String header, @RequestParam Long groupId);

    @GetMapping("api/v1/user-group/role")
    boolean isUserCoordinator(@RequestHeader("innerCommunication") String header, @RequestParam(name = "groupId")Long groupId, @RequestParam(name = "userId")Long userId);

    @GetMapping( "api/v1/user-group/group")
    Boolean isUserPartOfTheGroup(@RequestHeader("innerCommunication") String header, @RequestParam("groupId") Long groupId, @RequestParam("userId") Long userId);

    @PostMapping( "api/v1/trip-group/availability")
    void setSelectedAvailability(@RequestHeader("innerCommunication") String header, @RequestBody SelectedAvailabilityDto selectedAvailabilityDto);
}
