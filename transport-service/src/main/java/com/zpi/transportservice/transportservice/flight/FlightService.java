package com.zpi.transportservice.transportservice.flight;

import com.zpi.transportservice.transportservice.transport.AirTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    public List<Flight> saveFlights(List<Flight> flight, AirTransport airTransport){
        for(var f : flight){
            f.setTransport(airTransport);
        }
        return flightRepository.saveAll(flight);
    }


}
