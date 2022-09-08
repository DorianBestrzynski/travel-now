package com.zpi.financeoptimizerservice.financeoptimazerservice.expenditure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
}
