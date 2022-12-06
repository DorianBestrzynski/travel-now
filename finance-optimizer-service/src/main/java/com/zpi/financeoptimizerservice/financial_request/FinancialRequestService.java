package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.aspects.AuthorizeAuthorOrCoordinatorRequest;
import com.zpi.financeoptimizerservice.aspects.AuthorizePartOfTheGroup;
import com.zpi.financeoptimizerservice.commons.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static com.zpi.financeoptimizerservice.exceptions.ExceptionsInfo.ENTITY_NOT_FOUND;
import static com.zpi.financeoptimizerservice.exceptions.ExceptionsInfo.INVALID_PARAMS;


@Service
@RequiredArgsConstructor
public class FinancialRequestService {

    private final FinancialRequestRepository financialRequestRepository;

    @Transactional
    public void addFinancialRequests(Long debteeId, Map<Long, Double> debts, Long groupId) {

        var financialRequestsToAdd = debts.entrySet().stream()
                .map(entry -> FinancialRequest.create(BigDecimal.valueOf(entry.getValue()), debteeId, entry.getKey(), groupId))
                .toList();

        financialRequestRepository.saveAll(financialRequestsToAdd);
    }

    public void addFinancialRequest(Long debteeId, Long debtorId, Double price, Long groupId) {
        var financialRequestToAdd = FinancialRequest.create(BigDecimal.valueOf(price), debteeId, debtorId, groupId);
        financialRequestRepository.save(financialRequestToAdd);
    }

    public Set<FinancialRequest> getAllActiveFinancialRequestsIn(Long groupId) {
        return financialRequestRepository.getAllActiveInGroup(groupId);
    }

    @AuthorizePartOfTheGroup
    public Set<FinancialRequest> getUserFinancialRequests(Long userId, Long groupId) {
        return financialRequestRepository.getAllByDebtorAndExpenditure(userId, groupId);
    }
    /**Do not delete groupId, it is used in aspects!!!**/
    @Transactional
    @AuthorizePartOfTheGroup
    @AuthorizeAuthorOrCoordinatorRequest
    public void acceptFinancialRequest(Long requestId, Long groupId) {
        var financialRequest = financialRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        financialRequest.setStatus(Status.RESOLVED);
        financialRequest.setGenerationDate(LocalDateTime.now());
        financialRequestRepository.save(financialRequest);
    }

    public void deleteAllFinancialRequests(Long groupId) {
        financialRequestRepository.deleteAllByGroupId(groupId);
    }

    @AuthorizePartOfTheGroup
    public Set<FinancialRequest> getAllUnsettledFinanceRequests(Long groupId) {
        return getAllActiveFinancialRequestsIn(groupId);
    }

    @AuthorizePartOfTheGroup
    public Boolean isDebtorOrDebteeToAnyFinancialRequests(Long groupId, Long userId) {
        if(groupId == null || userId == null)
            throw new IllegalArgumentException(INVALID_PARAMS);
        var financialRequestSet =  financialRequestRepository.getAllByDebtorAndExpenditure(userId, groupId);
        return !financialRequestSet.isEmpty();
    }

    @AuthorizePartOfTheGroup
    public Set<FinancialRequest> getAllFinancialRequestInGroup(Long groupId, Long userId) {
        return financialRequestRepository.getAllFinancialRequestInGroup(groupId);
    }
}
