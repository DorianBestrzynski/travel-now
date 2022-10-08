package com.zpi.accommodationservice.accommodationservice.proxies;

import com.zpi.accommodationservice.accommodationservice.configuration.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "transport", url = "${app.group-service}:8087/api/v1/transport",configuration = CustomFeignConfiguration.class)
public interface TransportProxy {

    @PostMapping

}
