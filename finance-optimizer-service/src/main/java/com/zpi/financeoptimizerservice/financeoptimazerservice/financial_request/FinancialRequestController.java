package com.zpi.financeoptimizerservice.financeoptimazerservice.financial_request;

import com.zpi.financeoptimizerservice.financeoptimazerservice.commons.Status;
import com.zpi.financeoptimizerservice.financeoptimazerservice.expenditure.Expenditure;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/finance-optimizer")
@RequiredArgsConstructor
public class FinancialRequestController {

    private final FinancialRequestRepository financialRequestRepository;

    private final FinancialRequestService financialRequestService;

    @GetMapping("/addfin")
    public String addExpenditure() {
        FinancialRequest financialRequest = new FinancialRequest(LocalDateTime.now(), Status.PENDING, BigDecimal.TEN, 2L, 1L, 1L);
        FinancialRequest financialRequest2 = new FinancialRequest(LocalDateTime.now(), Status.PENDING, BigDecimal.ONE, 3L, 1L, 1L);

        financialRequestRepository.saveAll(List.of(financialRequest, financialRequest2));
        return "Added financial request";

    }
}
