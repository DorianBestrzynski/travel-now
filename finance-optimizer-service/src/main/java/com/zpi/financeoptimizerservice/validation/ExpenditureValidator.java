package com.zpi.financeoptimizerservice.validation;
import com.zpi.financeoptimizerservice.dto.ExpenditureInputDto;
import com.zpi.financeoptimizerservice.exceptions.ExpenditureValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ExpenditureValidator {

    public void validateExpenditureInput(ExpenditureInputDto expenditureInput, Long groupId) {
        checkIfFieldsArePresent(expenditureInput);
        validateTitleOf(expenditureInput);
        validatePriceOf(expenditureInput);
    }

    private void checkIfFieldsArePresent(ExpenditureInputDto expenditureInput) {
        var title = expenditureInput.title();
        if (title == null || title.isBlank()) {
            throw ExpenditureValidationException.blankField("title");
        }

        var price = expenditureInput.price();
        if (price == null) {
            throw ExpenditureValidationException.blankField("price");
        }

        var debtorsIds = expenditureInput.debtorsIds();
        if (debtorsIds == null) {
            throw ExpenditureValidationException.blankField("debtorsIds");
        }
        if (debtorsIds.isEmpty()) {
            throw ExpenditureValidationException.noDebtors();
        }
    }

    private void validateTitleOf(ExpenditureInputDto expenditureInput) {
        if (expenditureInput.title().length() > 60) {
            throw ExpenditureValidationException.titleOutOfBounds();
        }
    }

    private void validatePriceOf(ExpenditureInputDto expenditureInput) {
        var price = expenditureInput.price();

        if (doesNotHaveTwoFractionDigits(price) || price > 999_999_999) {
            throw ExpenditureValidationException.priceOutOfBounds();
        }

        if (doesNotHaveTwoFractionDigits(expenditureInput.pricePerDebtor())) {
            throw ExpenditureValidationException.pricePerDebtorOutOfBounds();
        }
    }

    private boolean doesNotHaveTwoFractionDigits(double price) {
        return BigDecimal.valueOf(price).compareTo(BigDecimal.valueOf(0.01d)) < 0;
    }

}
