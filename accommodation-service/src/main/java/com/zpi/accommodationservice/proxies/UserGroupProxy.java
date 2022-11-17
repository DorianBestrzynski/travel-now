package com.zpi.accommodationservice.proxies;

import com.zpi.accommodationservice.configuration.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-group", url = "${app.group-service}:8082/api/v1/user-group",configuration = CustomFeignConfiguration.class)
public interface UserGroupProxy {
    @GetMapping("/group")
    Boolean isUserPartOfTheGroup(@RequestHeader("innerCommunication") String header, @RequestParam("groupId") Long groupId, @RequestParam("userId") Long userId);

    @GetMapping("/role")
    boolean isUserCoordinator(@RequestHeader("innerCommunication") String header,@RequestParam(name = "groupId") Long groupId, @RequestParam(name = "userId") Long userId);
}
