package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.commons.Status;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class FinancialRequestOptimizerTest {

    @MockBean
    private FinancialRequestRepository financialRequestRepository;

    @MockBean
    private FinancialRequestService financialRequestService;

    @Autowired
    @InjectMocks
    private FinancialRequestOptimizer financialRequestOptimizer;


    @Test
    void shouldReturnCorrectNetCashFlow() {
        //given
        FinancialRequest financialRequest = new FinancialRequest(BigDecimal.valueOf(1000), 0L, 1L, 1L, Status.PENDING);
        FinancialRequest financialRequest1 = new FinancialRequest(BigDecimal.valueOf(2000), 0L, 2L, 1L, Status.PENDING);
        FinancialRequest financialRequest2 = new FinancialRequest(BigDecimal.valueOf(5000), 1L, 2L, 1L, Status.PENDING);
        Set<FinancialRequest> financialRequests = Set.of(financialRequest, financialRequest1, financialRequest2);

        //when
        var result = financialRequestOptimizer.calculateNetCashFlowIn(financialRequests);

        //then
        Map<Long, Double> resultMap = Map.of(0L, -3000.00, 1L, -4000.00, 2L, 7000.00);

        assertThat(result).isEqualTo(resultMap);
    }

    @Test
    void shouldCorrectlyConvertPricesToBigDecimal(){
        //given
        Map<Long, Double> inputMap = Map.of(0L, -3000.00, 1L, -4000.00, 2L, 7000.00);

        //when
        var result = financialRequestOptimizer.convertPricesToBigDecimal(inputMap);

        //then
        var expectedBalance = Map.of(0L,
                BigDecimal.valueOf(-3000.00).setScale(2, RoundingMode.CEILING),
                1L, BigDecimal.valueOf(-4000.00).setScale(2, RoundingMode.CEILING),
                2L, BigDecimal.valueOf(7000.00).setScale(2, RoundingMode.CEILING));

        assertTrue(areMapsEqual(result, expectedBalance));
    }

    @Test
    void shouldCorrectlyConvertEmptyMap(){
        //given
        Map<Long, Double> inputMap = Map.of();

        //when
        var result = financialRequestOptimizer.convertPricesToBigDecimal(inputMap);

        //then
        Map<Long, BigDecimal> expectedBalance = Map.of();

        assertTrue(areMapsEqual(result, expectedBalance));
    }

    @Test
    void shouldOptimizeFinancialRequestIn(){
        //given
        FinancialRequest financialRequest = new FinancialRequest(BigDecimal.valueOf(1000), 0L, 1L, 1L, Status.PENDING);
        FinancialRequest financialRequest1 = new FinancialRequest(BigDecimal.valueOf(2000), 0L, 2L, 1L, Status.PENDING);
        FinancialRequest financialRequest2 = new FinancialRequest(BigDecimal.valueOf(5000), 1L, 2L, 1L, Status.PENDING);
        Set<FinancialRequest> financialRequests = Set.of(financialRequest, financialRequest1, financialRequest2);

        //when
        when(financialRequestService.getAllActiveFinancialRequestsIn(anyLong())).thenReturn(financialRequests);
        financialRequestOptimizer.optimizeFinancialRequestsIn(1L);

        //then
        verify(financialRequestService, times(1)).getAllActiveFinancialRequestsIn(anyLong());
        verify(financialRequestRepository, times(1)).deleteAll(anySet());
        verify(financialRequestService, times(2)).addFinancialRequest(anyLong(), anyLong(), anyDouble(), anyLong());
    }

    @Test
    void shouldNotOptimizeFinancialRequestIn(){
        //given
        FinancialRequest financialRequest = new FinancialRequest(BigDecimal.valueOf(1000), 0L, 3L, 1L, Status.PENDING);
        FinancialRequest financialRequest1 = new FinancialRequest(BigDecimal.valueOf(2000), 1L, 3L, 1L, Status.PENDING);
        FinancialRequest financialRequest2 = new FinancialRequest(BigDecimal.valueOf(5000), 2L, 3L, 1L, Status.PENDING);
        Set<FinancialRequest> financialRequests = Set.of(financialRequest, financialRequest1, financialRequest2);

        //when
        when(financialRequestService.getAllActiveFinancialRequestsIn(anyLong())).thenReturn(financialRequests);
        financialRequestOptimizer.optimizeFinancialRequestsIn(1L);

        //then
        verify(financialRequestService, times(1)).getAllActiveFinancialRequestsIn(anyLong());
        verify(financialRequestRepository, times(1)).deleteAll(anySet());
        verify(financialRequestService, times(3)).addFinancialRequest(anyLong(), anyLong(), anyDouble(), anyLong());
    }

    private boolean areMapsEqual(Map<Long, BigDecimal> first, Map<Long, BigDecimal> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
}