package com.airline.airline_management_system.controller;

import com.airline.airline_management_system.entity.Booking;
import com.airline.airline_management_system.entity.BookingSeat;
import com.airline.airline_management_system.repository.BookingRepository;
import com.airline.airline_management_system.util.TicketGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class TicketController {

    private final BookingRepository bookingRepository;
    private final TicketGenerator ticketGenerator;

    @GetMapping("/{id}/ticket")
    public ResponseEntity<byte[]> downloadTicket(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) throws Exception {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new RuntimeException("Access denied");
        }

        BookingSeat firstSeat = booking.getBookingSeats().get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        byte[] pdfBytes = ticketGenerator.generateTicketPdf(
                booking.getPnr(),
                firstSeat.getPassengerName(),
                booking.getFlight().getFlightNumber(),
                booking.getFlight().getRoute().getSourceAirport().getCode(),
                booking.getFlight().getRoute().getDestinationAirport().getCode(),
                booking.getFlight().getDepartureTime().format(formatter),
                firstSeat.getSeat().getSeatNumber()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket_" + booking.getPnr() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}