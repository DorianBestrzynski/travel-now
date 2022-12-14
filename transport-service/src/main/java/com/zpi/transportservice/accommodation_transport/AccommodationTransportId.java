package com.zpi.transportservice.accommodation_transport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccommodationTransportId implements Serializable {

    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(name = "transport_id")
    private Long transportId;
}
