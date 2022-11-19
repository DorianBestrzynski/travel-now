package com.zpi.financeoptimizerservice.testconfig;

import com.zpi.financeoptimizerservice.commons.Status;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequest;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeData {
    @Value("${spring.profiles.active:default}")
    private String profile;
    private final FinancialRequestRepository financialRequestRepository;

    @PostConstruct
    public void addAccommodations() {
        if (!profile.equals("test"))
            return;

        var financialRequests = List.of(
               new FinancialRequest(BigDecimal.TEN, 1L, 2L, 1L, Status.PENDING)
        );

        financialRequestRepository.saveAll(financialRequests);
    }
}
