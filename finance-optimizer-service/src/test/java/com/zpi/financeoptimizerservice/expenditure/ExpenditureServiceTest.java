package com.zpi.financeoptimizerservice.expenditure;

import com.zpi.financeoptimizerservice.commons.Status;
import com.zpi.financeoptimizerservice.dto.ExpenditureInputDto;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequest;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestOptimizer;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestService;
import com.zpi.financeoptimizerservice.proxies.UserGroupProxy;
import com.zpi.financeoptimizerservice.security.CustomUsernamePasswordAuthenticationToken;
import com.zpi.financeoptimizerservice.validation.ExpenditureValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class ExpenditureServiceTest {

    @MockBean
    ExpenditureRepository expenditureRepository;

    @MockBean
    ExpenditureValidator expenditureValidator;

    @MockBean
    FinancialRequestService financialRequestService;

    @MockBean
    FinancialRequestOptimizer financialRequestOptimizer;

    @Autowired
    @InjectMocks
    ExpenditureService expenditureService;

    @MockBean
    UserGroupProxy userGroupProxy;


    void mockAuthorizePartOfTheGroupAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserPartOfTheGroup(anyString(), anyLong(), anyLong());
    }

    void mockAuthorizeAuthorOrCoordinatorExpenditureAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(expenditureRepository.findById(anyLong())).thenReturn(Optional.of(new Expenditure()));
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserCoordinator(anyString(), anyLong(), anyLong());
    }

    @Test
    void shouldReturnExpendituresMetadata() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var expenditureSet = Set.of(new Expenditure("Title", BigDecimal.TEN, 1L, 1L, List.of(1L, 2L)));

        //when
        when(expenditureRepository.findAllByGroupId(anyLong())).thenReturn(expenditureSet);
        var result = expenditureService.getExpendituresMetadata(1L);

        //then
        assertThat(result).isEqualTo(expenditureSet);
        verify(expenditureRepository, times(1)).findAllByGroupId(anyLong());
    }

    @Test
    void shouldAddCorrectlyExpenditure() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var expenditureInput = new ExpenditureInputDto(1L, "Title", 10.0, List.of(1L, 2L));

        //when
        when(expenditureRepository.save(any(Expenditure.class))).thenAnswer(i -> i.getArguments()[0]);
        var actualResult = expenditureService.addExpenditure(1L, expenditureInput);

        //then
        var expectedResult = new Expenditure("Title", BigDecimal.valueOf(10.0), 1L, 1L, List.of(1L, 2L));
        assertThat(actualResult).satisfies(
                actual -> {
                    assertThat(actual.getCreatorId()).isEqualTo(expectedResult.getCreatorId());
                    assertThat(actual.getTitle()).isEqualTo(expectedResult.getTitle());
                    assertThat(actual.getPrice()).isEqualTo(expectedResult.getPrice());
                    assertThat(actual.getGroupId()).isEqualTo(expectedResult.getGroupId());
                    assertThat(actual.getExpenseDebtors()).isEqualTo(expectedResult.getExpenseDebtors());
                }
        );
        verify(expenditureRepository, times(1)).save(any(Expenditure.class));
        verify(expenditureValidator, times(1)).validateExpenditureInput(any(ExpenditureInputDto.class));
        verify(financialRequestService, times(1)).addFinancialRequests(anyLong(), anyMap(), anyLong());
        verify(financialRequestOptimizer, times(1)).optimizeFinancialRequestsIn(anyLong());
    }

    @Test
    void shouldEditExpenditureAndRegenerateFinancialRequestWhenPriceChanged() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        mockAuthorizeAuthorOrCoordinatorExpenditureAspect();
        var expenditureInput = new ExpenditureInputDto(1L, "Title", 10.0, List.of(1L, 2L));
        var existingExpenditure = new Expenditure("Title", BigDecimal.ONE, 1L, 1L, List.of(1L, 2L));

        //when
        when(expenditureRepository.findById(anyLong())).thenReturn(Optional.of(existingExpenditure));
        when(expenditureRepository.save(any(Expenditure.class))).thenAnswer(i -> i.getArguments()[0]);
        var actualResult = expenditureService.editExpenditure(1L, 1L, expenditureInput);

        //then
        var expectedResult = new Expenditure("Title", BigDecimal.valueOf(10.0), 1L, 1L, List.of(1L, 2L));
        assertThat(actualResult).satisfies(
                actual -> {
                    assertThat(actual.getCreatorId()).isEqualTo(expectedResult.getCreatorId());
                    assertThat(actual.getTitle()).isEqualTo(expectedResult.getTitle());
                    assertThat(actual.getPrice()).isEqualTo(expectedResult.getPrice());
                    assertThat(actual.getGroupId()).isEqualTo(expectedResult.getGroupId());
                    assertThat(actual.getExpenseDebtors()).isEqualTo(expectedResult.getExpenseDebtors());
                }
        );
        verify(expenditureRepository, times(1)).save(any(Expenditure.class));
        verify(expenditureRepository, times(2)).findById(anyLong());
        verify(financialRequestService, times(1)).deleteAllFinancialRequests(anyLong());
        verify(financialRequestOptimizer, times(1)).optimizeFinancialRequestsIn(anyLong());
    }

    @Test
    void shouldEditExpenditureAndNotRegenerateFinancialRequestsWhenTitleChanges() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        mockAuthorizeAuthorOrCoordinatorExpenditureAspect();
        var expenditureInput = new ExpenditureInputDto(null, "Changed Title", null, null);
        var existingExpenditure = new Expenditure("Title", BigDecimal.valueOf(10.0), 1L, 1L, List.of(1L, 2L));

        //when
        when(expenditureRepository.findById(anyLong())).thenReturn(Optional.of(existingExpenditure));
        when(expenditureRepository.save(any(Expenditure.class))).thenAnswer(i -> i.getArguments()[0]);
        var actualResult = expenditureService.editExpenditure(1L, 1L, expenditureInput);

        //then
        var expectedResult = new Expenditure("Changed Title", BigDecimal.valueOf(10.0), 1L, 1L, List.of(1L, 2L));
        assertThat(actualResult).satisfies(
                actual -> {
                    assertThat(actual.getCreatorId()).isEqualTo(expectedResult.getCreatorId());
                    assertThat(actual.getTitle()).isEqualTo(expectedResult.getTitle());
                    assertThat(actual.getPrice()).isEqualTo(expectedResult.getPrice());
                    assertThat(actual.getGroupId()).isEqualTo(expectedResult.getGroupId());
                    assertThat(actual.getExpenseDebtors()).isEqualTo(expectedResult.getExpenseDebtors());
                }
        );
        verify(expenditureRepository, times(1)).save(any(Expenditure.class));
        verify(expenditureRepository, times(2)).findById(anyLong());
        verify(financialRequestService, never()).deleteAllFinancialRequests(anyLong());
        verify(financialRequestOptimizer, never()).optimizeFinancialRequestsIn(anyLong());
    }

    @Test
    void shouldDeleteExpenditure() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        mockAuthorizeAuthorOrCoordinatorExpenditureAspect();

        //when
        expenditureService.deleteExpenditure(1L,1L);

        //then
        verify(expenditureRepository, times(1)).delete(any(Expenditure.class));
        verify(financialRequestService, times(1)).deleteAllFinancialRequests(anyLong());
        verify(financialRequestOptimizer, times(1)).optimizeFinancialRequestsIn(anyLong());

    }

    @Test
    void getGroupBalance() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        FinancialRequest financialRequest = new FinancialRequest(BigDecimal.valueOf(1000), 0L, 1L, 1L, Status.PENDING);
        FinancialRequest financialRequest1 = new FinancialRequest(BigDecimal.valueOf(2000), 0L, 2L, 1L, Status.PENDING);
        FinancialRequest financialRequest2 = new FinancialRequest(BigDecimal.valueOf(5000), 1L, 2L, 1L, Status.PENDING);
        Set<FinancialRequest> financialRequests = Set.of(financialRequest, financialRequest1, financialRequest2);

        //when
        when(financialRequestService.getAllActiveFinancialRequestsIn(anyLong())).thenReturn(financialRequests);
        when(financialRequestOptimizer.calculateNetCashFlowIn(financialRequests)).thenCallRealMethod();
        when(financialRequestOptimizer.convertPricesToBigDecimal(financialRequestOptimizer.calculateNetCashFlowIn(financialRequests))).thenCallRealMethod();
        var actualBalance = expenditureService.getGroupBalance(1L);

        //then
        var expectedBalance = Map.of(0L, BigDecimal.valueOf(-3000.00).setScale(2, RoundingMode.CEILING), 1L, BigDecimal.valueOf(-4000.00).setScale(2, RoundingMode.CEILING), 2L, BigDecimal.valueOf(7000.00).setScale(2, RoundingMode.CEILING));
        assertTrue(areMapsEqual(actualBalance, expectedBalance));
        verify(financialRequestService, times(1)).getAllActiveFinancialRequestsIn(anyLong());
        verify(financialRequestOptimizer, times(2)).calculateNetCashFlowIn(anySet());
        verify(financialRequestOptimizer, times(1)).convertPricesToBigDecimal(anyMap());

    }

    private boolean areMapsEqual(Map<Long, BigDecimal> first, Map<Long, BigDecimal> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
}