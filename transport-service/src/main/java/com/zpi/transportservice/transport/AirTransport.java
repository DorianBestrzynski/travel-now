package com.zpi.transportservice.transport;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zpi.transportservice.flight.Flight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue(value = "1")
@NoArgsConstructor
@Setter
@Getter
public class AirTransport extends Transport {

    @OneToMany(cascade=CascadeType.ALL, fetch= FetchType.LAZY)
    @JsonManagedReference
    private List<Flight> flight;

    public AirTransport(Duration duration, BigDecimal price, String source, String destination, LocalDate startDate, LocalDate endDate, String link, List<Flight> flight) {
        super(duration, price, source, destination, startDate, endDate, link);
        this.flight = flight;
    }
}
