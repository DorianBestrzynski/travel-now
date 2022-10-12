package com.zpi.transportservice.transportservice.proxy;

import com.zpi.transportservice.transportservice.configuration.CustomFeignConfiguration;
import com.zpi.transportservice.transportservice.dto.TripDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/api/v1/trip-group",configuration = CustomFeignConfiguration.class)
public interface TripGroupProxy {

    @GetMapping("/data")
    ResponseEntity<TripDataDto> getTripData(@RequestParam Long groupId);

}
