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
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue(value = "3")
@NoArgsConstructor
@Setter
@Getter
public class UserTransport extends Transport {

    @Column(length = 100)
    private String meanOfTransport;

    @Column()
    private String description;

    @Column
    private LocalDateTime meetingTime;

    public UserTransport(Duration duration, BigDecimal price, String source, String destination, LocalDate startDate, LocalDate endDate, String link, String meanOfTransport, String description, LocalDateTime meetingTime) {
        super(duration, price, source, destination, startDate, endDate, link);
        this.meanOfTransport = meanOfTransport;
        this.description = description;
        this.meetingTime = meetingTime;
    }
}
