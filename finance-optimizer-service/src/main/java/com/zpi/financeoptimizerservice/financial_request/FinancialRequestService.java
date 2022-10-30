package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.commons.Status;
import com.zpi.financeoptimizerservice.exceptions.ApiPermissionException;
import com.zpi.financeoptimizerservice.proxies.UserGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.zpi.financeoptimizerservice.exceptions.ExceptionsInfo.NOT_A_GROUP_MEMBER;
import static com.zpi.financeoptimizerservice.exceptions.ExceptionsInfo.PERMISSION_VIOLATION;


@Service
@RequiredArgsConstructor
public class FinancialRequestService {

    private final FinancialRequestRepository financialRequestRepository;
    private final UserGroupProxy userGroupProxy;

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

    public boolean hasFinancialRequests(Long userId, Long groupId){
        return !getUserFinancialRequests(userId, groupId).isEmpty();
    }

    public Set<FinancialRequest> getUserFinancialRequests(Long userId, Long groupId) {
        return financialRequestRepository.getAllByDebtorAndExpenditure(userId, groupId);
    }

    @Transactional
    public void acceptFinancialRequest(Long requestId, Long userId, Long groupId) {
        if (!userGroupProxy.isUserPartOfTheGroup(groupId, userId)) {
            throw new ApiPermissionException(NOT_A_GROUP_MEMBER);
        }
        var financialRequest = financialRequestRepository.findById(requestId).orElseThrow();
        if (!(financialRequest.getDebtee().equals(userId) || userGroupProxy.isUserCoordinator(groupId, userId))) {
            throw new ApiPermissionException(PERMISSION_VIOLATION);
        }
        financialRequest.setStatus(Status.RESOLVED);
        financialRequestRepository.save(financialRequest);
    }

    public void deleteAllFinancialRequests(Long groupId) {
        financialRequestRepository.deleteAllByGroupId(groupId);
    }

    public Set<FinancialRequest> getAllActiveInGroup(Long groupId) {
        return financialRequestRepository.getAllActiveInGroup(groupId);
    }
}
