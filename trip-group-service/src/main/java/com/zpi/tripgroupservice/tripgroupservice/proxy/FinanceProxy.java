package com.zpi.tripgroupservice.tripgroupservice.proxy;

import com.zpi.tripgroupservice.tripgroupservice.config.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "finance-optimizer", url = "${app.finance-service}:8086/api/v1/finance-request",configuration = CustomFeignConfiguration.class)
public interface FinanceProxy {

    @GetMapping("/user")
    Boolean isDebtorOrDebteeToAnyFinancialRequests(@RequestParam Long groupId, @RequestParam Long userId);
}
