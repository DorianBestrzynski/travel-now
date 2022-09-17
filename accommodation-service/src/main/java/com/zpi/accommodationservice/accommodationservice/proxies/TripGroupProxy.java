package com.zpi.accommodationservice.accommodationservice.proxies;

import com.zpi.accommodationservice.accommodationservice.configuration.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/api/v1/user-group",configuration = CustomFeignConfiguration.class)
public interface TripGroupProxy {
    @GetMapping( "/group")
    Boolean isUserPartOfTheGroup(@RequestParam("groupId") Long groupId, @RequestParam("userId") Long userId);

    @GetMapping("/role")
    boolean isUserCoordinator(@RequestParam(name = "groupId")Long groupId, @RequestParam(name = "userId")Long userId);
}
