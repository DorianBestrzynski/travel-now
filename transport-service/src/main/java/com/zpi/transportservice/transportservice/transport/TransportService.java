package com.zpi.transportservice.transportservice.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportService {

    private final TransportRepository transportRepository;
}
