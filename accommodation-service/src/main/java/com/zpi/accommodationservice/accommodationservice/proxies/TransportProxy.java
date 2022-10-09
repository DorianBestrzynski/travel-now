package com.zpi.accommodationservice.accommodationservice.proxies;

import com.zpi.accommodationservice.accommodationservice.configuration.CustomFeignConfiguration;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationInfoDto;
import com.zpi.accommodationservice.accommodationservice.dto.TransportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "transport", url = "${app.group-service}:8087/api/v1/transport",configuration = CustomFeignConfiguration.class)
public interface TransportProxy {

    @PostMapping()
    boolean generateTransportForAccommodation(AccommodationInfoDto accommodationInfoDto);

}
