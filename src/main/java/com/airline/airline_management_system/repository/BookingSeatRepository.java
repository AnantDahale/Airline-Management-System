package com.airline.airline_management_system.repository;

import com.airline.airline_management_system.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;


public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("SELECT bs FROM BookingSeat bs WHERE bs.seat.flight.id = :flightId AND bs.booking.status = 'CONFIRMED'")
    List<BookingSeat> findConfirmedByFlightId(Long flightId);
}