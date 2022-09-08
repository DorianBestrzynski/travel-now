package com.zpi.financeoptimizerservice.financeoptimazerservice.financial_request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialRequestRepository extends JpaRepository<FinancialRequest, Long> {
}
