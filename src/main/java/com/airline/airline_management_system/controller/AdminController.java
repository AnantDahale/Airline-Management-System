package com.airline.airline_management_system.controller;

import com.airline.airline_management_system.dto.response.AnalyticsResponse;
import com.airline.airline_management_system.enums.BookingStatus;
import com.airline.airline_management_system.repository.BookingRepository;
import com.airline.airline_management_system.repository.FlightRepository;
import com.airline.airline_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        long totalBookings = bookingRepository.count();
        long confirmed = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long cancelled = bookingRepository.countByStatus(BookingStatus.CANCELLED);
        long totalFlights = flightRepository.count();
        long totalUsers = userRepository.count();
        var revenue = bookingRepository.getTotalRevenue();

        return ResponseEntity.ok(new AnalyticsResponse(
                totalBookings, confirmed, cancelled, revenue, totalFlights, totalUsers
        ));
    }
}