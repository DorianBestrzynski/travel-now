package com.zpi.dayplanservice.proxies;

import com.zpi.dayplanservice.configuration.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-group", url = "${app.trip-group-service}:8082/api/v1/user-group", configuration = CustomFeignConfiguration.class)
public interface TripGroupProxy {

    @GetMapping("/role")
    boolean isUserCoordinator(@RequestParam(name = "groupId")Long groupId, @RequestParam(name = "userId")Long userId);

    @GetMapping( "/group")
    Boolean isUserPartOfTheGroup(@RequestParam("groupId") Long groupId, @RequestParam("userId") Long userId);


}


