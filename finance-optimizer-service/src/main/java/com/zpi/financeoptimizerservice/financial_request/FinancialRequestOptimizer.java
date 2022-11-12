package com.zpi.financeoptimizerservice.financial_request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FinancialRequestOptimizer {

    private final FinancialRequestRepository financialRequestRepository;
    private final FinancialRequestService financialRequestService;

    public void optimizeFinancialRequestsIn(Long groupId) {
        var oldFinancialRequests = financialRequestService.getAllActiveFinancialRequestsIn(groupId);
        Map<Long, Double> netCashFlow = calculateNetCashFlowIn(oldFinancialRequests);
        financialRequestRepository.deleteAll(oldFinancialRequests);
        optimizeNetCashFlow(convertPricesToBigDecimal(netCashFlow), groupId);
    }

    public Map<Long, Double> calculateNetCashFlowIn(Set<FinancialRequest> financialRequests) {
        Map<Long, Double> result = new HashMap<>();

        for (var financialRequest : financialRequests) {
            var debteeId = financialRequest.getDebtee();
            var debtorId = financialRequest.getDebtor();
            var price = financialRequest.getAmount().doubleValue();
            result.compute(debteeId, (id, cashFlow) -> (cashFlow) == null ? price : cashFlow + price);
            result.compute(debtorId, (id, cashFlow) -> (cashFlow) == null ? -price : cashFlow - price);
        }

        return result;
    }

    public Map<Long, BigDecimal> convertPricesToBigDecimal(Map<Long, Double> netCashFlow) {
        Map<Long, BigDecimal> result = new HashMap<>();
        for (var entry : netCashFlow.entrySet()) {
            var price = BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.CEILING);
            result.put(entry.getKey(), price);
        }
        return result;
    }

    private void optimizeNetCashFlow(Map<Long, BigDecimal> netCashFlow, Long groupId) {
        while (netCashFlow.size() > 1) {
            var maxReceiver = getMaxReceiverFrom(netCashFlow);
            var maxGiver = getMaxGiverFrom(netCashFlow);

            var maxReceiverCashFlow = maxReceiver.getValue();
            assert maxReceiverCashFlow.compareTo(BigDecimal.ZERO) > 0;

            var maxGiverCashFlow = maxGiver.getValue();
            assert maxGiverCashFlow.compareTo(BigDecimal.ZERO) < 0;

            var debteeId = maxReceiver.getKey();
            var debtorId = maxGiver.getKey();
            var cashFlowDifference = maxReceiverCashFlow.add(maxGiverCashFlow);
            var nextRequestPrice = maxGiverCashFlow.abs();

            if (cashFlowDifference.compareTo(BigDecimal.ZERO) > 0) {
                var newCashFlow = maxReceiverCashFlow.add(maxGiverCashFlow);
                netCashFlow.remove(debtorId);
                netCashFlow.put(debteeId, newCashFlow);
            } else if (cashFlowDifference.compareTo(BigDecimal.ZERO) < 0) {
                var newCashFlow = maxGiverCashFlow.add(maxReceiverCashFlow);
                netCashFlow.remove(debteeId);
                netCashFlow.put(debtorId, newCashFlow);
                nextRequestPrice = maxReceiverCashFlow;
            } else {
                netCashFlow.remove(debtorId);
                netCashFlow.remove(debteeId);
            }

            financialRequestService.addFinancialRequest(debteeId, debtorId, nextRequestPrice.doubleValue(), groupId);
        }
    }

    private Map.Entry<Long, BigDecimal> getMaxReceiverFrom(Map<Long, BigDecimal> netCashFlow) {
        return netCashFlow.entrySet().stream().max(comparingByNetCashFlow())
                .orElseThrow();
    }

    private Map.Entry<Long, BigDecimal> getMaxGiverFrom(Map<Long, BigDecimal> netCashFlow) {
        return netCashFlow.entrySet().stream().min(comparingByNetCashFlow())
                .orElseThrow();
    }

    private Comparator<Map.Entry<Long, BigDecimal>> comparingByNetCashFlow() {
        return Map.Entry.comparingByValue();
    }
}

