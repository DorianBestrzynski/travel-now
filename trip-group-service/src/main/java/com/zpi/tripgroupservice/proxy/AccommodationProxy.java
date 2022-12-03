package com.zpi.tripgroupservice.proxy;

import com.zpi.tripgroupservice.config.CustomFeignConfiguration;
import com.zpi.tripgroupservice.dto.AccommodationDto;
import com.zpi.tripgroupservice.dto.AccommodationInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "accommodation", url = "${app.accommodation-service}:8088/api/v1/accommodation",configuration = CustomFeignConfiguration.class)
public interface AccommodationProxy {

    @GetMapping("/info")
    AccommodationInfoDto getAccommodationInfo(@RequestHeader("innerCommunication") String header, @RequestParam Long accommodationId);

    @DeleteMapping("/user-votes")
    void deleteAllVotesForUserInGivenGroup(@RequestHeader("innerCommunication") String header, @RequestParam Long userId, @RequestParam Long groupId);

    @GetMapping()
    AccommodationDto getAccommodation(@RequestHeader("innerCommunication") String header, @RequestParam Long accommodationId);
}
