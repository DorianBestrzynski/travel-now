package com.zpi.availabilityservice.proxies;

import com.zpi.availabilityservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "user", url = "${app.user-service}:8081/api/v1/user")
public interface AppUserProxy {
    @PostMapping("/users")
    List<UserDto> getUsersDtos(List<Long> usersIds);
}
