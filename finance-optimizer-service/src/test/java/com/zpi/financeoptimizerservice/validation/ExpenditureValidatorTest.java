package com.zpi.financeoptimizerservice.validation;

import com.zpi.financeoptimizerservice.dto.ExpenditureInputDto;
import com.zpi.financeoptimizerservice.exceptions.ExpenditureValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExpenditureValidatorTest {

    private final ExpenditureValidator expenditureValidator = new ExpenditureValidator();

    @Test
    void shouldNotThrowExceptionWhenExpenditureInputIsValid() {
        ExpenditureInputDto expenditureInputDto = new ExpenditureInputDto(1L, "Title", 12.22, List.of(1L, 2L));
        assertDoesNotThrow(() -> expenditureValidator.validateExpenditureInput(expenditureInputDto));
    }

    @Test
    void shouldThrowExceptionWhenSomeFieldIsNotPresent() {
        ExpenditureInputDto expenditureInputDto = new ExpenditureInputDto(1L, null, 12.22, List.of(1L, 2L));
        ExpenditureValidationException exception = assertThrows(ExpenditureValidationException.class,
                () -> expenditureValidator.validateExpenditureInput(expenditureInputDto));
        assertEquals("Field title cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTitleOutOfBounds() {
        String outOfBoundsString = "longStringlongStringlongStringlongStringlongStringlongStringlongStringlongString";
        ExpenditureInputDto expenditureInputDto = new ExpenditureInputDto(1L, outOfBoundsString, 12.22, List.of(1L, 2L));
        ExpenditureValidationException exception = assertThrows(ExpenditureValidationException.class,
                () -> expenditureValidator.validateExpenditureInput(expenditureInputDto));
        assertEquals("Title has to be at most 60 characters long", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceOutOfBounds() {
        ExpenditureInputDto expenditureInputDto = new ExpenditureInputDto(1L, "Title", 0.001, List.of(1L, 2L));
        ExpenditureValidationException exception = assertThrows(ExpenditureValidationException.class,
                () -> expenditureValidator.validateExpenditureInput(expenditureInputDto));
        assertEquals("Price has to be in range [0.01, 999_999_999]", exception.getMessage());
    }


}