package com.zpi.transportservice.transport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Entity
@DiscriminatorValue(value = "2")
@NoArgsConstructor
@Setter
@Getter
public class CarTransport extends Transport {

    @Column(name = "distance_km")
    private Long distanceInKm;

    public CarTransport(Duration duration, Long distanceInKm, BigDecimal price, String source, String destination, LocalDate startDate, LocalDate endDate, String link) {
        super(duration, price, source, destination, startDate, endDate, link, 2);
        this.distanceInKm = distanceInKm;
    }

    public CarTransport(Long transportId, Duration duration, Long distanceInKm, BigDecimal price, String source, String destination, LocalDate startDate, LocalDate endDate, String link) {
        super(transportId, duration, price, source, destination, startDate, endDate, link, 2);
        this.distanceInKm = distanceInKm;
    }
}
