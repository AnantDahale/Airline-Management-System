package com.airline.airline_management_system.repository;

import com.airline.airline_management_system.entity.Seat;
import com.airline.airline_management_system.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightId(Long flightId);
    List<Seat> findByFlightIdAndStatus(Long flightId, SeatStatus status);
}