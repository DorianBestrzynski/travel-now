package com.zpi.financeoptimizerservice.financeoptimazerservice.expenditure;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/finance-optimizer")
@RequiredArgsConstructor
public class ExpenditureController {

    private final ExpenditureRepository expenditureRepository;

    @GetMapping("/addexp")
    public String addExpenditure(){
        Expenditure expenditure = new Expenditure(LocalDateTime.now(), "Hello", BigDecimal.ONE, 1L, 1L);
        expenditureRepository.save(expenditure);
        return "Expenditure Saved";
    }
}
