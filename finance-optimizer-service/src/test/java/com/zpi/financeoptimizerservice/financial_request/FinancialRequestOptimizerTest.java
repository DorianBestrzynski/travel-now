package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.commons.Status;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FinancialRequestOptimizerTest {

    @Autowired
    private FinancialRequestOptimizer financialRequestOptimizer;

    @Test
    void shouldReturnCorrectNetCashFlow() {
        FinancialRequest financialRequest = new FinancialRequest(BigDecimal.valueOf(1000), 0L, 1L, 1L, Status.PENDING);
        FinancialRequest financialRequest1 = new FinancialRequest(BigDecimal.valueOf(2000), 0L, 2L, 1L, Status.PENDING);
        FinancialRequest financialRequest2 = new FinancialRequest(BigDecimal.valueOf(5000), 1L, 2L, 1L, Status.PENDING);
        Set<FinancialRequest> financialRequests = Set.of(financialRequest, financialRequest1, financialRequest2);

        var result = financialRequestOptimizer.calculateNetCashFlowIn(financialRequests);

        Map<Long, Double> resultMap = Map.of(0L, -3000.00, 1L, -4000.00, 2L, 7000.00);

        assertThat(result).isEqualTo(resultMap);
    }
}