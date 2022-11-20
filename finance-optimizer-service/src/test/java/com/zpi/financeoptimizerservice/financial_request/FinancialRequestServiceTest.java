package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.commons.Status;
import com.zpi.financeoptimizerservice.proxies.UserGroupProxy;
import com.zpi.financeoptimizerservice.security.CustomUsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class FinancialRequestServiceTest {

    @MockBean
    FinancialRequestRepository financialRequestRepository;

    @Autowired
    @InjectMocks
    FinancialRequestService financialRequestService;

    @Captor
    ArgumentCaptor<List<FinancialRequest>> financialRequestList;

    @Captor
    ArgumentCaptor<FinancialRequest> financialRequest;

    @MockBean
    UserGroupProxy userGroupProxy;

    void mockAuthorizePartOfTheGroupAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserPartOfTheGroup(anyString(), anyLong(), anyLong());
    }

    void mockAuthorizeAuthorOrCoordinatorRequestAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(financialRequestRepository.findById(anyLong())).thenReturn(Optional.of(new FinancialRequest()));
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserCoordinator(anyString(), anyLong(), anyLong());
    }

    @Test
    void shouldAddFinancialRequests() {
        //given
        var debts = Map.of(0L, -3000.00, 1L, -4000.00, 3L, 7000.00);

        //when
        financialRequestService.addFinancialRequests(2L, debts, 1L);

        //then
        var expectedFinancialRequests =  List.of(new FinancialRequest(BigDecimal.valueOf(7000).setScale(1, RoundingMode.CEILING), 3L, 2L, 1L, Status.PENDING),
                new FinancialRequest(BigDecimal.valueOf(-4000).setScale(1, RoundingMode.CEILING), 1L, 2L, 1L, Status.PENDING),
                new FinancialRequest(BigDecimal.valueOf(-3000).setScale(1, RoundingMode.CEILING), 0L, 2L, 1L, Status.PENDING));
        verify(financialRequestRepository, times(1)).saveAll(financialRequestList.capture());
        var actualFinancialRequests = financialRequestList.getValue();
        assertThat(actualFinancialRequests).hasSameElementsAs(expectedFinancialRequests);
    }

    @Test
    void shouldAddSingleFinancialRequest() {
        //when
        financialRequestService.addFinancialRequest(1L,2L, 15.0, 1L);

        //then
        var expectedFinancialRequest = new FinancialRequest(BigDecimal.valueOf(15.0).setScale(1,
                RoundingMode.CEILING),
                2L,
                1L,
                1L,
                Status.PENDING);
        verify(financialRequestRepository, times(1)).save(financialRequest.capture());
        var actualFinancialRequest = financialRequest.getValue();
        assertThat(actualFinancialRequest).isEqualTo(expectedFinancialRequest);
    }

    @Test
    void shouldReturnAllActiveFinancialRequests() {
        //given
        var financialRequests = Set.of(new FinancialRequest(BigDecimal.valueOf(-4000).setScale(1, RoundingMode.CEILING), 1L, 2L, 1L, Status.PENDING));
        //when
        when(financialRequestRepository.getAllActiveInGroup(anyLong())).thenReturn(financialRequests);
        var actualResult = financialRequestService.getAllActiveFinancialRequestsIn(1L);

        //then
        verify(financialRequestRepository, times(1)).getAllActiveInGroup(anyLong());
        assertThat(actualResult).hasSameElementsAs(financialRequests);
    }

    @Test
    void shouldReturnAllExpendituresWhereUserIsDebtorOrDebtee() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var financialRequests = Set.of(new FinancialRequest(BigDecimal.valueOf(-4000).setScale(1, RoundingMode.CEILING), 1L, 2L, 1L, Status.PENDING));

        //when
        when(financialRequestRepository.getAllByDebtorAndExpenditure(anyLong(), anyLong())).thenReturn(financialRequests);
        var actualResult = financialRequestService.getUserFinancialRequests(1L, 1L);

        //then
        verify(financialRequestRepository, times(1)).getAllByDebtorAndExpenditure(anyLong(), anyLong());
        assertThat(actualResult).hasSameElementsAs(financialRequests);
    }

    @Test
    void shouldAcceptFinancialRequest() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        mockAuthorizeAuthorOrCoordinatorRequestAspect();

        //when
        financialRequestService.acceptFinancialRequest(1L);

        //then
        verify(financialRequestRepository, times(2)).findById(anyLong());
        verify(financialRequestRepository, times(1)).save(financialRequest.capture());
        assertThat(Status.RESOLVED).isEqualTo(financialRequest.getValue().getStatus());
    }

    @Test
    void shouldDeleteAllFinancialRequests() {
        //when
        financialRequestService.deleteAllFinancialRequests(1L);

        //then
        verify(financialRequestRepository, times(1)).deleteAllByGroupId(1L);
    }

    @Test
    void shouldGetAllUnsettledExpenses() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var financialRequests = Set.of(new FinancialRequest(BigDecimal.valueOf(-4000).setScale(1, RoundingMode.CEILING), 1L, 2L, 1L, Status.PENDING));
        //when
        when(financialRequestRepository.getAllActiveInGroup(anyLong())).thenReturn(financialRequests);
        var actualResult = financialRequestService.getAllUnsettledFinanceRequests(1L);

        //then
        verify(financialRequestRepository, times(1)).getAllActiveInGroup(anyLong());
        assertThat(actualResult).isEqualTo(financialRequests);
    }

    @Test
    void shouldGetAllExpensesInGroup() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var financialRequests = Set.of(new FinancialRequest(BigDecimal.valueOf(-4000).setScale(1, RoundingMode.CEILING), 1L, 2L, 1L, Status.RESOLVED));
        //when
        when(financialRequestRepository.getAllFinancialRequestInGroup(anyLong())).thenReturn(financialRequests);
        var actualResult = financialRequestService.getAllFinancialRequestInGroup(1L, 1L);

        //then
        verify(financialRequestRepository, times(1)).getAllFinancialRequestInGroup(anyLong());
        assertThat(actualResult).isEqualTo(financialRequests);
    }

    @Test
    void shouldReturnTrueThatIsDebtorOrDebteeToAnyRequests() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var financialRequests = Set.of(new FinancialRequest(BigDecimal.valueOf(-4000).setScale(1, RoundingMode.CEILING), 1L, 2L, 1L, Status.PENDING));

        //when
        when(financialRequestRepository.getAllByDebtorAndExpenditure(anyLong(), anyLong())).thenReturn(financialRequests);
        var actualResult = financialRequestService.isDebtorOrDebteeToAnyFinancialRequests(1L, 1L);

        //then
        verify(financialRequestRepository, times(1)).getAllByDebtorAndExpenditure(1L, 1L);
        assertTrue(actualResult);
    }

    @Test
    void shouldReturnFalseThatIsNotDebtorOrDebteeToAnyRequests() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        Set<FinancialRequest> financialRequests = Set.of();

        //when
        when(financialRequestRepository.getAllByDebtorAndExpenditure(anyLong(), anyLong())).thenReturn(financialRequests);
        var actualResult = financialRequestService.isDebtorOrDebteeToAnyFinancialRequests(1L, 1L);

        //then
        verify(financialRequestRepository, times(1)).getAllByDebtorAndExpenditure(1L, 1L);
        assertFalse(actualResult);
    }

}