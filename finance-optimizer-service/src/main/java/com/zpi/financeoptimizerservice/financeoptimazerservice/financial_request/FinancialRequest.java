package com.zpi.financeoptimizerservice.financeoptimazerservice.financial_request;

import com.zpi.financeoptimizerservice.financeoptimazerservice.commons.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class FinancialRequest {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "request_sequence"
    )
    @SequenceGenerator(
            name = "request_sequence",
            sequenceName = "request_sequence", allocationSize = 10)
    @Column(name = "financial_request_id",unique = true, nullable = false)
    private Long financialRequestId;

    @Column(name = "generation_date")
    private LocalDateTime generationDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "debtor", nullable = false)
    private Long debtor;

    @Column(name = "debtee", nullable = false)
    private Long debtee;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    public FinancialRequest(LocalDateTime generationDate, Status status, BigDecimal amount, Long debtor, Long debtee, Long groupId) {
        this.generationDate = generationDate;
        this.status = status;
        this.amount = amount;
        this.debtor = debtor;
        this.debtee = debtee;
        this.groupId = groupId;
    }
}
