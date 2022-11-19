package com.zpi.tripgroupservice.proxy;

import com.zpi.tripgroupservice.config.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "finance-optimizer", url = "${app.finance-service}:8086/api/v1/finance-request",configuration = CustomFeignConfiguration.class)
public interface FinanceProxy {

    @GetMapping("/user")
    Boolean isDebtorOrDebteeToAnyFinancialRequests(@RequestHeader("innerCommunication") String header, @RequestParam Long groupId, @RequestParam Long userId);
}
