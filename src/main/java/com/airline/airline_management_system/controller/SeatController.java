package com.airline.airline_management_system.controller;

import com.airline.airline_management_system.dto.response.SeatResponse;
import com.airline.airline_management_system.entity.Seat;
import com.airline.airline_management_system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights/{flightId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatRepository seatRepository;

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getSeatsByFlight(@PathVariable Long flightId) {
        List<Seat> seats = seatRepository.findByFlightId(flightId);
        List<SeatResponse> response = seats.stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatNumber(), s.getSeatClass(), s.getStatus()))
                .toList();
        return ResponseEntity.ok(response);
    }
}