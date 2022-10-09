package com.zpi.accommodationservice.accommodationservice.proxies;

import com.zpi.accommodationservice.accommodationservice.configuration.CustomFeignConfiguration;
import com.zpi.accommodationservice.accommodationservice.dto.TripDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/api/v1/trip-group",configuration = CustomFeignConfiguration.class)
public interface TripGroupProxy {

    @GetMapping("/data")
    ResponseEntity<TripDataDto> getTripData(@RequestParam Long groupId);

}
