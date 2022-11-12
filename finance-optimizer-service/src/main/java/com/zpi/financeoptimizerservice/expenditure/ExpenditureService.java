package com.zpi.financeoptimizerservice.expenditure;

import com.zpi.financeoptimizerservice.aspects.AuthorizeAuthorOrCoordinatorExpenditure;
import com.zpi.financeoptimizerservice.aspects.AuthorizePartOfTheGroup;
import com.zpi.financeoptimizerservice.dto.ExpenditureInputDto;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestOptimizer;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestService;
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
    private final ExpenditureValidator expenditureValidator;
    private final FinancialRequestService financialRequestService;
    private final FinancialRequestOptimizer financialRequestOptimizer;


    @AuthorizePartOfTheGroup
    public Set<Expenditure> getExpendituresMetadata(Long groupId, Long userId) {
        return expenditureRepository.findAllByGroupId(groupId);
    }

    @Transactional
    @AuthorizePartOfTheGroup
    public Expenditure addExpenditure(Long groupId, ExpenditureInputDto expenditureInput) {
        var expenditure = mapInputToExpenditure(expenditureInput, groupId);
        var addedExpenditure = expenditureRepository.save(expenditure);
        createFinancialRequestsFrom(expenditureInput, groupId);
        financialRequestOptimizer.optimizeFinancialRequestsIn(groupId);
        return addedExpenditure;

    }

    private Expenditure mapInputToExpenditure(ExpenditureInputDto expenditureInputDto, Long groupId) {
        expenditureValidator.validateExpenditureInput(expenditureInputDto);

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
    @AuthorizePartOfTheGroup
    @AuthorizeAuthorOrCoordinatorExpenditure
    public Expenditure editExpenditure(Long groupId, Long expenditureId, Long userId, ExpenditureInputDto expenditureInput) {
        var expenditure = expenditureRepository.findById(expenditureId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
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
    @AuthorizePartOfTheGroup
    @AuthorizeAuthorOrCoordinatorExpenditure
    public void deleteExpenditure(Long expenditureId, Long userId, Long groupId) {
        var expenditure = expenditureRepository.findById(expenditureId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        expenditureRepository.delete(expenditure);
        regenerateOptimizationProcess(groupId);
    }

    private void regenerateOptimizationProcess(Long groupId) {
        financialRequestService.deleteAllFinancialRequests(groupId);
        var allExpenditures = expenditureRepository.findAllByGroupId(groupId);
        for (var ex : allExpenditures){
            createFinancialRequestsFrom(new ExpenditureInputDto(ex.getCreatorId(), ex.getTitle(), ex.getPrice().doubleValue(), ex.getExpenseDebtors()), groupId);
        }
        financialRequestOptimizer.optimizeFinancialRequestsIn(groupId);
    }

    @AuthorizePartOfTheGroup
    public Map<Long, BigDecimal> getGroupBalance(Long groupId, Long userId) {
        var financialRequests = financialRequestService.getAllActiveInGroup(groupId);
        var balanceDouble = financialRequestOptimizer.calculateNetCashFlowIn(financialRequests);
        return financialRequestOptimizer.convertPricesToBigDecimal(balanceDouble);
    }
}
