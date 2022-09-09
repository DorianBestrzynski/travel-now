package com.zpi.financeoptimizerservice.financeoptimazerservice.expenditure;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime generationDate;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    public Expenditure(LocalDateTime generationDate, String title, BigDecimal price, Long groupId, Long creatorId) {
        this.generationDate = generationDate;
        this.title = title;
        this.price = price;
        this.groupId = groupId;
        this.creatorId = creatorId;
    }
}
