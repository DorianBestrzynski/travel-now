package com.zpi.financeoptimizerservice.financial_request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FinancialRequestRepository extends JpaRepository<FinancialRequest, Long> {
    @Query("from FinancialRequest fr where fr.status = 'PENDING' and fr.groupId = :groupId")
    Set<FinancialRequest> getAllActiveInGroup(Long groupId);

    @Query("from FinancialRequest f where (f.debtor = :debtorId  or f.debtee = :debtorId) and f.groupId = :groupId and f.status = 'PENDING'")
    Set<FinancialRequest> getAllByDebtorAndExpenditure(@Param("debtorId") Long debtorId, @Param("groupId") Long groupId);

    void deleteAllByGroupId(Long groupId);

}
