package com.zpi.transportservice.transportservice.accommodation_transport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationTransport {

    @EmbeddedId
    private AccommodationTransportId id;
}
