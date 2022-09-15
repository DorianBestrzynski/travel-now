package com.zpi.accommodationservice.accommodationservice.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/api/v1/trip-group")
public interface TripGroupProxy {
    @GetMapping( "group/{groupId}/{userId}")
    Boolean isUserPartOfTheGroup(@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId);
}
