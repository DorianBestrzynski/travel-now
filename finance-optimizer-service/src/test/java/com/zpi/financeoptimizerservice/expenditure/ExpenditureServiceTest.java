package com.zpi.financeoptimizerservice.expenditure;

import com.zpi.financeoptimizerservice.financial_request.FinancialRequestOptimizer;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestService;
import com.zpi.financeoptimizerservice.proxies.UserGroupProxy;
import com.zpi.financeoptimizerservice.validation.ExpenditureValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ExpenditureServiceTest {

    @Mock
    ExpenditureRepository expenditureRepository;

    @Mock
    UserGroupProxy userGroupProxy;

    @Mock
    ExpenditureValidator expenditureValidator;

    @Mock
    FinancialRequestService financialRequestService;

    @Mock
    FinancialRequestOptimizer financialRequestOptimizer;

    @InjectMocks
    ExpenditureService expenditureService;

    @Test
    void getExpendituresMetadata() {
    }

    @Test
    void addExpenditure() {
    }

    @Test
    void editExpenditure() {
    }

    @Test
    void deleteExpenditure() {
    }

    @Test
    void getGroupBalance() {
    }
}