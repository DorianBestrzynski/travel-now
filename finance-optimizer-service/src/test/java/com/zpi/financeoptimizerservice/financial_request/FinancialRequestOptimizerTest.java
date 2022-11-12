package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.commons.Status;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FinancialRequestOptimizerTest {

    @MockBean
    private FinancialRequestRepository financialRequestRepository;

    @MockBean
    private FinancialRequestService financialRequestService;

    @InjectMocks
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