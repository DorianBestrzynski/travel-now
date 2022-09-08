package com.zpi.financeoptimizerservice.financeoptimazerservice.financial_request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialRequestService {

    private final FinancialRequestRepository financialRequestRepository;
}
