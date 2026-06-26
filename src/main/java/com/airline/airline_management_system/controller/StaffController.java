package com.airline.airline_management_system.controller;

import com.airline.airline_management_system.dto.response.ManifestResponse;
import com.airline.airline_management_system.entity.BookingSeat;
import com.airline.airline_management_system.repository.BookingSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final BookingSeatRepository bookingSeatRepository;

    @GetMapping("/flights/{flightId}/manifest")
    public ResponseEntity<List<ManifestResponse>> getManifest(@PathVariable Long flightId) {
        List<BookingSeat> bookingSeats = bookingSeatRepository.findConfirmedByFlightId(flightId);

        List<ManifestResponse> manifest = bookingSeats.stream()
                .map(bs -> new ManifestResponse(
                        bs.getPassengerName(),
                        bs.getPassengerAge(),
                        bs.getSeat().getSeatNumber(),
                        bs.getSeat().getSeatClass().name(),
                        bs.getBooking().getPnr(),
                        bs.getBooking().getStatus().name()
                ))
                .toList();

        return ResponseEntity.ok(manifest);
    }
}