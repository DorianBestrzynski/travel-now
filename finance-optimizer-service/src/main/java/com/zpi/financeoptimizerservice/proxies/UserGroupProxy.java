package com.zpi.financeoptimizerservice.proxies;

import com.zpi.financeoptimizerservice.configuration.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-group", url = "${app.group-service}:8082/api/v1/user-group",configuration = CustomFeignConfiguration.class)
public interface UserGroupProxy {
    @GetMapping("/group")
    Boolean isUserPartOfTheGroup(@RequestParam("groupId") Long groupId, @RequestParam("userId") Long userId);

    @GetMapping("/role")
    boolean isUserCoordinator(@RequestParam(name = "groupId") Long groupId, @RequestParam(name = "userId") Long userId);
}
