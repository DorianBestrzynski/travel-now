package com.zpi.financeoptimizerservice.financeoptimazerservice.expenditure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
