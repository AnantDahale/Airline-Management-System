package com.airline.airline_management_system.repository;

import com.airline.airline_management_system.entity.Booking;
import com.airline.airline_management_system.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPnr(String pnr);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatusAndLockExpiresAtBefore(BookingStatus status, LocalDateTime time);
}