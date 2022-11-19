package com.zpi.financeoptimizerservice.expenditure;

import com.zpi.financeoptimizerservice.dto.ExpenditureInputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/v1/finance-optimizer")
@RequiredArgsConstructor
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    @GetMapping()
    public ResponseEntity<Set<Expenditure>> getExpendituresMetadata(@RequestParam Long groupId) {
        var expenditures = expenditureService.getExpendituresMetadata(groupId);
        return ResponseEntity.ok(expenditures);
    }

    @PostMapping()
    public ResponseEntity<Expenditure> addExpenditure(@RequestParam Long groupId, @RequestBody ExpenditureInputDto expenditureInput) {
        var result = expenditureService.addExpenditure(groupId, expenditureInput);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping()
    public ResponseEntity<Expenditure> editExpenditure(@RequestParam Long groupId, @RequestParam Long expenditureId, @RequestBody ExpenditureInputDto expenditureInput){
        var result = expenditureService.editExpenditure(groupId, expenditureId, expenditureInput);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteExpenditure(@RequestParam Long expenditureId, @RequestParam Long groupId){
        expenditureService.deleteExpenditure(expenditureId, groupId);
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<Long, BigDecimal>> getGroupBalance(@RequestParam Long groupId){
        var result = expenditureService.getGroupBalance(groupId);
        return ResponseEntity.ok(result);
    }

}
