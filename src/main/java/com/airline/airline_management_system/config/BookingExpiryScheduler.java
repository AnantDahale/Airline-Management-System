package com.airline.airline_management_system.config;

import com.airline.airline_management_system.entity.Booking;
import com.airline.airline_management_system.entity.BookingSeat;
import com.airline.airline_management_system.enums.BookingStatus;
import com.airline.airline_management_system.enums.SeatStatus;
import com.airline.airline_management_system.repository.BookingRepository;
import com.airline.airline_management_system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingExpiryScheduler {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredHolds() {
        List<Booking> expired = bookingRepository.findByStatusAndLockExpiresAtBefore(
                BookingStatus.PENDING, LocalDateTime.now()
        );

        for (Booking booking : expired) {
            for (BookingSeat bs : booking.getBookingSeats()) {
                bs.getSeat().setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(bs.getSeat());
            }
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }

        if (!expired.isEmpty()) {
            System.out.println("Released " + expired.size() + " expired booking holds");
        }
    }
}