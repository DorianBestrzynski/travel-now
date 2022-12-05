package com.zpi.transportservice.transport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorColumn(name = "transport_type",
                    discriminatorType = DiscriminatorType.INTEGER)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Transport {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transport_sequence"
    )
    @SequenceGenerator(
            name = "transport_sequence",
            sequenceName = "transport_sequence", allocationSize = 10)
    @Column(name = "transport_id",unique = true, nullable = false)
    private Long transportId;

    @Column(name = "duration")
    private Duration duration;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "source", length = 70)
    private String source;

    @Column(name = "destination", length = 70)
    private String destination;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "link")
    private String link;

    @Column(name = "transport_type_json")
    private Integer transportTypeJson;


    public Transport(Duration duration, BigDecimal price, String source, String destination, LocalDate startDate, LocalDate endDate , String link, Integer transportType) {
        this.duration = duration;
        this.price = price;
        this.source = source;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.link = link;
        this.transportTypeJson = transportType;
    }

    public Transport(Long transportId, Duration duration, BigDecimal price, String source, String destination, LocalDate startDate, LocalDate endDate , String link, Integer transportType) {
        this.transportId = transportId;
        this.duration = duration;
        this.price = price;
        this.source = source;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.link = link;
        this.transportTypeJson = transportType;
    }
}
