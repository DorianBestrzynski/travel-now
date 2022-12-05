package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.commons.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/finance-request")
@RequiredArgsConstructor
public class FinancialRequestController {

    private final FinancialRequestRepository financialRequestRepository;

    private final FinancialRequestService financialRequestService;

    @GetMapping("/addfin")
    public String addExpenditure() {
        FinancialRequest financialRequest = new FinancialRequest(BigDecimal.TEN, 2L, 1L, 1L, Status.PENDING);
        FinancialRequest financialRequest2 = new FinancialRequest(BigDecimal.ZERO, 3L, 1L, 1L, Status.PENDING);

        financialRequestRepository.saveAll(List.of(financialRequest, financialRequest2));
        return "Added financial request";
    }

    @PatchMapping("/accept")
    public ResponseEntity<?> acceptRequest(@RequestParam Long requestId, @RequestParam Long groupId) {
        financialRequestService.acceptFinancialRequest(requestId, groupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unsettled")
    public ResponseEntity<Set<FinancialRequest>> getAllUnsettledFinanceRequests(@RequestParam Long groupId){
        var result = financialRequestService.getAllUnsettledFinanceRequests(groupId);
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    public ResponseEntity<Set<FinancialRequest>> getAllFinancialRequestInGroup(@RequestParam Long groupId, @RequestParam Long userId){
        var result = financialRequestService.getAllFinancialRequestInGroup(groupId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user")
    public ResponseEntity<Boolean> isDebtorOrDebteeToAnyFinancialRequests(@RequestParam Long groupId, @RequestParam Long userId){
        var result = financialRequestService.isDebtorOrDebteeToAnyFinancialRequests(groupId, userId);
        return ResponseEntity.ok(result);
    }

}
