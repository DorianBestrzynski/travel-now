package com.zpi.dayplanservice.proxies;

import com.zpi.dayplanservice.configuration.CustomFeignConfiguration;
import com.zpi.dayplanservice.dto.AccommodationInfoDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-group", url = "${app.trip-group-service}:8082/api/v1", configuration = CustomFeignConfiguration.class)
public interface TripGroupProxy {

    @GetMapping("/user-group/role")
    boolean isUserCoordinator(@RequestHeader("innerCommunication") String header, @RequestParam(name = "groupId")Long groupId, @RequestParam(name = "userId")Long userId);

    @GetMapping( "/user-group/group")
    Boolean isUserPartOfTheGroup(@RequestHeader("innerCommunication") String header, @RequestParam("groupId") Long groupId, @RequestParam("userId") Long userId);

    @GetMapping( "/trip-group/accommodation")
    AccommodationInfoDto getGroupAccommodation(@RequestHeader("innerCommunication") String header, @RequestParam("groupId") Long groupId);
}


