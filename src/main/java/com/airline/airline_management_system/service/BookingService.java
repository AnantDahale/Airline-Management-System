package com.airline.airline_management_system.service;

import com.airline.airline_management_system.dto.request.BookingHoldRequest;
import com.airline.airline_management_system.dto.response.BookingResponse;

import java.util.List;


public interface BookingService {
    BookingResponse holdSeats(String userEmail, BookingHoldRequest request);
    BookingResponse confirmBooking(String userEmail, Long bookingId);
    BookingResponse cancelBooking(String userEmail, Long bookingId);
    BookingResponse getByPnr(String pnr);
    List<BookingResponse> getMyBookings(String userEmail);
}