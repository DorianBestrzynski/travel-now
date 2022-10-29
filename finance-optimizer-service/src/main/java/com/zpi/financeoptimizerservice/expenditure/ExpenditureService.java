package com.zpi.financeoptimizerservice.expenditure;

import com.zpi.financeoptimizerservice.dto.ExpenditureInputDto;
import com.zpi.financeoptimizerservice.exceptions.ApiPermissionException;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestOptimizer;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestService;
import com.zpi.financeoptimizerservice.proxies.UserGroupProxy;
import com.zpi.financeoptimizerservice.validation.ExpenditureValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.zpi.financeoptimizerservice.exceptions.ExceptionsInfo.*;

@Service
@RequiredArgsConstructor
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final UserGroupProxy userGroupProxy;
    private final ExpenditureValidator expenditureValidator;
    private final FinancialRequestService financialRequestService;
    private final FinancialRequestOptimizer financialRequestOptimizer;


    public Set<Expenditure> getExpendituresMetadata(Long groupId, Long userId) {
        if(!userGroupProxy.isUserPartOfTheGroup(groupId, userId)){
            throw new ApiPermissionException(NOT_A_GROUP_MEMBER);
        }
        return expenditureRepository.findAllByGroupId(groupId);
    }

    @Transactional
    public Expenditure addExpenditure(Long groupId, ExpenditureInputDto expenditureInput) {
        if(!userGroupProxy.isUserPartOfTheGroup(groupId, expenditureInput.creatorId())){
            throw new ApiPermissionException(NOT_A_GROUP_MEMBER);
        }
        var expenditure = mapInputToExpenditure(expenditureInput, groupId);
        var addedExpenditure = expenditureRepository.save(expenditure);
        createFinancialRequestsFrom(expenditureInput, groupId);
        financialRequestOptimizer.optimizeFinancialRequestsIn(groupId);
        return addedExpenditure;

    }

    private Expenditure mapInputToExpenditure(ExpenditureInputDto expenditureInputDto, Long groupId) {
        expenditureValidator.validateExpenditureInput(expenditureInputDto, groupId);

        return new Expenditure(expenditureInputDto.title(),
                Optional.ofNullable(expenditureInputDto.price()).map(BigDecimal::valueOf).orElse(null),
                groupId,
                expenditureInputDto.creatorId(),
                expenditureInputDto.debtorsIds());
    }
    private void createFinancialRequestsFrom(ExpenditureInputDto expenditureInput, Long groupId) {
        Long debtee = expenditureInput.creatorId();
        Map<Long, Double> debts = new HashMap<>();

        for (var debtorId : expenditureInput.debtorsIds()) {
            var pricePerDebtor = BigDecimal.valueOf(expenditureInput.pricePerDebtor())
                    .setScale(2, RoundingMode.CEILING)
                    .doubleValue();
            debts.put(debtorId, pricePerDebtor);
        }

        financialRequestService.addFinancialRequests(debtee, debts, groupId);
    }

    @Transactional
    public Expenditure editExpenditure(Long groupId, Long expenditureId, Long userId, ExpenditureInputDto expenditureInput) {
        if(!userGroupProxy.isUserPartOfTheGroup(groupId, userId)){
            throw new ApiPermissionException(NOT_A_GROUP_MEMBER);
        }
        var expenditure = expenditureRepository.findById(expenditureId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        if (!(userGroupProxy.isUserCoordinator(groupId, userId) || Objects.equals(expenditure.getCreatorId(), userId))) {
            throw new ApiPermissionException(PERMISSION_VIOLATION);
        }
        var shouldTriggerRequests = updateExpenditure(expenditure, expenditureInput);
        var updatedExpenditure = expenditureRepository.save(expenditure);
        if(shouldTriggerRequests) {
            regenerateOptimizationProcess(groupId);
        }
        return updatedExpenditure;
    }

    private boolean updateExpenditure(Expenditure expenditure, ExpenditureInputDto expenditureInput) {
        boolean shouldTriggerCreatingFinanceRequests = false;
        if(Objects.nonNull(expenditureInput.creatorId())){
            expenditure.setCreatorId((expenditureInput.creatorId()));
            shouldTriggerCreatingFinanceRequests = true;
        }
        if(Objects.nonNull(expenditureInput.price())){
            expenditure.setPrice((BigDecimal.valueOf(expenditureInput.price())));
            shouldTriggerCreatingFinanceRequests = true;
        }
        if(Objects.nonNull(expenditureInput.title())){
            expenditure.setTitle((expenditureInput.title()));
        }
        if(Objects.nonNull(expenditureInput.debtorsIds())){
            expenditure.setExpenseDebtors((expenditureInput.debtorsIds()));
            shouldTriggerCreatingFinanceRequests = true;
        }
        return shouldTriggerCreatingFinanceRequests;
    }

    @Transactional
    public void deleteExpenditure(Long expenditureId, Long userId, Long groupId) {
        var expenditure = expenditureRepository.findById(expenditureId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        if (!userGroupProxy.isUserPartOfTheGroup(groupId, userId)) {
            throw new ApiPermissionException(NOT_A_GROUP_MEMBER);
        }
        if (!(userGroupProxy.isUserCoordinator(groupId, userId) || Objects.equals(expenditure.getCreatorId(), userId))) {
            throw new ApiPermissionException(PERMISSION_VIOLATION);
        }
        expenditureRepository.delete(expenditure);
        regenerateOptimizationProcess(groupId);
    }

    private void regenerateOptimizationProcess(Long groupId) {
        financialRequestService.deleteAllFinancialRequests();
        var allExpenditures = expenditureRepository.findAllByGroupId(groupId);
        for (var ex : allExpenditures){
            createFinancialRequestsFrom(new ExpenditureInputDto(ex.getCreatorId(), ex.getTitle(), ex.getPrice().doubleValue(), ex.getExpenseDebtors()), groupId);
        }
        financialRequestOptimizer.optimizeFinancialRequestsIn(groupId);
    }
}
