package com.zpi.financeoptimizerservice.dto;

import java.util.List;

public record ExpenditureInputDto(Long creatorId, String title, Double price, List<Long> debtorsIds) {

    public int numberOfDebtors() {
        return debtorsIds == null ? 0 : debtorsIds.size();
    }

    public double pricePerDebtor() {
        if (price != null && numberOfDebtors() != 0) {
            return price / numberOfDebtors();
        }
        return 0;
    }

}
