package com.zpi.tripgroupservice.proxy;

import com.zpi.tripgroupservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "user", url = "${app.user-service}:8081/api/v1/user")
public interface AppUserProxy {
    @PostMapping("/users")
    List<UserDto> getUsersDtos(@RequestHeader("innerCommunication") String header, List<Long> usersIds);
}
