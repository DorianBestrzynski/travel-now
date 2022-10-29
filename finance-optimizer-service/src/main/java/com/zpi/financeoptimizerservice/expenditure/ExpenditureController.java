package com.zpi.financeoptimizerservice.expenditure;

import com.zpi.financeoptimizerservice.dto.ExpenditureInputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/finance-optimizer")
@RequiredArgsConstructor
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    @GetMapping()
    public ResponseEntity<Set<Expenditure>> getExpendituresMetadata(@RequestParam Long groupId, @RequestParam Long userId) {
        var expenditures = expenditureService.getExpendituresMetadata(groupId, userId);
        return ResponseEntity.ok(expenditures);
    }

    @PostMapping()
    public ResponseEntity<Expenditure> addExpenditure(@RequestParam Long groupId, @RequestBody ExpenditureInputDto expenditureInput) {
        var result = expenditureService.addExpenditure(groupId, expenditureInput);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping()
    public ResponseEntity<Expenditure> editExpenditure(@RequestParam Long groupId, @RequestParam Long expenditureId, @RequestParam Long userId, @RequestBody ExpenditureInputDto expenditureInput){
        var result = expenditureService.editExpenditure(groupId, expenditureId, userId, expenditureInput);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteExpenditure(@RequestParam Long expenditureId, @RequestParam Long userId, @RequestParam Long groupId){
        expenditureService.deleteExpenditure(expenditureId, userId, groupId);
    }

}
