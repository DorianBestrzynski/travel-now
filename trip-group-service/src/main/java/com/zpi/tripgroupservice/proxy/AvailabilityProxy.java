package com.zpi.tripgroupservice.proxy;

import com.zpi.tripgroupservice.config.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "availability", url = "${app.availability-service}:8085/api/v1/availability",configuration = CustomFeignConfiguration.class)
public interface AvailabilityProxy {
    @DeleteMapping("/all-user")
    void deleteAllAvailabilitiesForUser(@RequestHeader("innerCommunication") String header, @RequestParam Long userId, @RequestParam Long groupId);

    @GetMapping("/triggerAvailabilityGeneration")
    String triggerAvailabilityGeneration(@RequestHeader("innerCommunication") String header, @RequestParam Long groupId);

    @GetMapping("/trigger-params")
    String triggerAvailabilityGenerationParams(@RequestHeader("innerCommunication") String header, @RequestParam Long groupId, @RequestParam(required = false) Integer minimalNumberOfDays,  @RequestParam(required = false) Integer minimalNumberOfParticipants );
}
