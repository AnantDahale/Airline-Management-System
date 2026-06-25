package com.airline.airline_management_system.repository;

import com.airline.airline_management_system.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Search flights by source airport code, destination airport code, and date range
    @org.springframework.data.jpa.repository.Query(
            "SELECT f FROM Flight f WHERE f.route.sourceAirport.code = :source " +
                    "AND f.route.destinationAirport.code = :destination " +
                    "AND f.departureTime BETWEEN :startOfDay AND :endOfDay"
    )
    List<Flight> searchFlights(String source, String destination, LocalDateTime startOfDay, LocalDateTime endOfDay);

    boolean existsByFlightNumber(String flightNumber);
}