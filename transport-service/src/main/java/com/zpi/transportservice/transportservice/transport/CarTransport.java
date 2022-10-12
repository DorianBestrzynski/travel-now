package com.zpi.transportservice.transportservice.transport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue(value = "2")
@NoArgsConstructor
@Setter
@Getter
public class CarTransport extends Transport {

    public CarTransport(Long transportId, Duration duration, BigDecimal price, String source, String destination, LocalDate startDate, LocalDate endDate, String link) {
        super(transportId, duration, price, source, destination, startDate, endDate, link);
    }
}
