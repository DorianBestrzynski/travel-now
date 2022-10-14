package com.zpi.transportservice.transportservice.proxy;

import com.zpi.transportservice.transportservice.configuration.CustomFeignConfiguration;
import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "accommodation", url = "${app.accommodation-service}:8088/api/v1/accommodation",configuration = CustomFeignConfiguration.class)
public interface AccommodationProxy {

    @GetMapping("/info")
    AccommodationInfoDto getAccommodationInfo(@RequestParam Long accommodationId);
}
