package com.zpi.financeoptimizerservice.expenditure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    Set<Expenditure> findAllByGroupId(Long groupId);
}
