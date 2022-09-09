package com.zpi.transportservice.transportservice.transport;

import com.zpi.transportservice.transportservice.commons.TransportType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Getter
@NoArgsConstructor
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

    @Column(name = "transport_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransportType transportType;

    @Column(name = "duration")
    private Duration duration;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "source", length = 70)
    private String source;

    @Column(name = "destination", length = 70)
    private String destination;

    @Column(name = "link")
    private String link;

    public Transport(TransportType transportType, Duration duration, BigDecimal price, String source, String destination, String link) {
        this.transportType = transportType;
        this.duration = duration;
        this.price = price;
        this.source = source;
        this.destination = destination;
        this.link = link;
    }
}
