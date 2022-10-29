package com.zpi.financeoptimizerservice.expenditure;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Expenditure {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "finance_sequence"
    )
    @SequenceGenerator(
            name = "finance_sequence",
            sequenceName = "finance_sequence", allocationSize = 10)
    @Column(name = "expenditure_id",unique = true, nullable = false)
    private Long expenditureId;

    @Column(name = "generation_date")
    private final LocalDateTime generationDate = LocalDateTime.now();

    @Setter
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Setter
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Setter
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Setter
    @ElementCollection
    @Column(name = "expense_debtors")
    private List<Long> expenseDebtors = new ArrayList<>();

    public Expenditure(String title, BigDecimal price, Long groupId, Long creatorId, List<Long> expenseDebtors) {
        this.title = title;
        this.price = price;
        this.groupId = groupId;
        this.creatorId = creatorId;
        this.expenseDebtors = expenseDebtors;
    }
}
