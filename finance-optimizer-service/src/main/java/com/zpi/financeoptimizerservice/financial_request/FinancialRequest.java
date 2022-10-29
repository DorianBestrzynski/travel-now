package com.zpi.financeoptimizerservice.financial_request;

import com.zpi.financeoptimizerservice.commons.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private final LocalDateTime generationDate = LocalDateTime.now();

    @Setter
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

    public FinancialRequest(BigDecimal amount, Long debtor, Long debtee, Long groupId, Status status) {
        this.amount = amount;
        this.debtor = debtor;
        this.debtee = debtee;
        this.groupId = groupId;
        this.status = status;
    }

    public static FinancialRequest create(BigDecimal price,
                                          Long debtee,
                                          Long debtor, Long groupId) {
        return new FinancialRequest(price, debtor, debtee, groupId, Status.PENDING);
    }
}
