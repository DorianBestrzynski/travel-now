package com.zpi.financeoptimizerservice.exceptions;

import org.springframework.http.HttpStatus;

public class ExpenditureValidationException extends ApiRestException {

    private ExpenditureValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static ExpenditureValidationException blankField(String fieldName) {
        var msg = "Field %s cannot be blank".formatted(fieldName);
        return new ExpenditureValidationException(msg);
    }

    public static ExpenditureValidationException noDebtors() {
        var msg = "Expenditure must have atleast one debtor";
        return new ExpenditureValidationException(msg);
    }

    public static ExpenditureValidationException titleOutOfBounds() {
        var msg = "Title has to be at most 60 characters long";
        return new ExpenditureValidationException(msg);
    }

    public static ExpenditureValidationException priceOutOfBounds() {
        var msg = "Price has to be in range [0.01, 999_999_999]";
        return new ExpenditureValidationException(msg);
    }

    public static ExpenditureValidationException pricePerDebtorOutOfBounds() {
        var msg = "Price per debtor has to be greater or equal to 0.01";
        return new ExpenditureValidationException(msg);
    }

    public static ExpenditureValidationException debtorNotInGroup(String userName) {
        var msg = "User %s is not a member of the group".formatted(userName);
        return new ExpenditureValidationException(msg);
    }
}
