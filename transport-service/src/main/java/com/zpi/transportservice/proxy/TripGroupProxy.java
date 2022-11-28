package com.zpi.transportservice.proxy;

import com.zpi.transportservice.configuration.CustomFeignConfiguration;
import com.zpi.transportservice.dto.TripDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/api/v1",configuration = CustomFeignConfiguration.class)
public interface TripGroupProxy {

    @GetMapping("/trip-group/transport-data")
    TripDataDto getTripData(@RequestHeader("innerCommunication") String header, @RequestParam Long groupId);

    @GetMapping("/user-group/role")
    boolean isUserCoordinator(@RequestHeader("innerCommunication") String header, @RequestParam(name = "groupId")Long groupId, @RequestParam(name = "userId")Long userId);


}
