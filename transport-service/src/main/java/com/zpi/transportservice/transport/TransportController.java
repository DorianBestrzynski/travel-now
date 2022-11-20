package com.zpi.transportservice.transport;

import com.zpi.transportservice.dto.UserTransportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService transportService;

    @PostMapping()
    public ResponseEntity<List<Transport>> getTransportForAccommodation(@RequestParam Long accommodationId) {
       var transport = transportService.getTransportForAccommodation(accommodationId);
       return ResponseEntity.ok(transport);
    }

    @PostMapping("/user-transport")
    public ResponseEntity<UserTransport> createUserTransport(@RequestParam Long accommodationId, @RequestBody UserTransportDto userTransportDto) {
        var transport = transportService.createUserTransport(accommodationId, userTransportDto);
        return ResponseEntity.ok(transport);
    }

    @PatchMapping("/user-transport")
    public ResponseEntity<UserTransport> changeUserTransport(@RequestParam Long transportId, @RequestBody UserTransportDto userTransportDto) {
        var transport = transportService.changeUserTransport(transportId, userTransportDto);
        return ResponseEntity.ok(transport);
    }

    @DeleteMapping("/user-transport")
    public void createUserTransport(@RequestParam Long accommodationId, @RequestParam Long transportId) {
        transportService.deleteUserTransport(accommodationId, transportId);
    }

}
