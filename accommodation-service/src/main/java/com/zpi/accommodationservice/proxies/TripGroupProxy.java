package com.zpi.accommodationservice.proxies;

import com.zpi.accommodationservice.configuration.CustomFeignConfiguration;
import com.zpi.accommodationservice.dto.TripGroupDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trip-group", url = "${app.group-service}:8082/api/v1/trip-group",configuration = CustomFeignConfiguration.class)
public interface TripGroupProxy {

    @PatchMapping("/accommodation")
    void setSelectedAccommodation(@RequestHeader("innerCommunication") String header, @RequestParam("groupId") Long groupId, @RequestParam("accommodationId") Long accommodationId);
}
