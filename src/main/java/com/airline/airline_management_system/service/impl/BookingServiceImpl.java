package com.airline.airline_management_system.service.impl;

import com.airline.airline_management_system.dto.request.BookingHoldRequest;
import com.airline.airline_management_system.dto.request.PassengerDetail;
import com.airline.airline_management_system.dto.response.*;
import com.airline.airline_management_system.entity.*;
import com.airline.airline_management_system.enums.BookingStatus;
import com.airline.airline_management_system.enums.SeatClass;
import com.airline.airline_management_system.enums.SeatStatus;
import com.airline.airline_management_system.exception.BadRequestException;
import com.airline.airline_management_system.exception.ResourceNotFoundException;
import com.airline.airline_management_system.exception.UnauthorizedException;
import com.airline.airline_management_system.repository.*;
import com.airline.airline_management_system.service.BookingService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final BigDecimal BUSINESS_CLASS_MULTIPLIER = new BigDecimal("2.5");
    private static final int LOCK_DURATION_MINUTES = 5;

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponse holdSeats(String userEmail, BookingHoldRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setPnr(generatePnr());
        booking.setStatus(BookingStatus.PENDING);
        booking.setLockExpiresAt(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BookingSeat> bookingSeats = new ArrayList<>();

        // Process each requested seat ONE AT A TIME with its own lock attempt
        for (PassengerDetail passenger : request.getPassengers()) {
            Seat seat = seatRepository.findById(passenger.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + passenger.getSeatId()));

            if (!seat.getFlight().getId().equals(flight.getId())) {
                throw new BadRequestException("Seat does not belong to the selected flight");
            }

            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BadRequestException("Seat " + seat.getSeatNumber() + " is no longer available");
            }

            // Attempt to lock the seat - this is where optimistic locking kicks in.
            // If another transaction modified this seat's version since we read it,
            // saving here will throw an exception.
            seat.setStatus(SeatStatus.LOCKED);
            try {
                seatRepository.save(seat); // version check happens here on flush/commit
            } catch (OptimisticLockingFailureException | OptimisticLockException ex) {
                throw new BadRequestException("Seat " + seat.getSeatNumber() + " was just taken by another passenger. Please choose a different seat.");
            }

            BigDecimal seatPrice = calculatePrice(flight, seat);
            totalAmount = totalAmount.add(seatPrice);

            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setPassengerName(passenger.getPassengerName());
            bookingSeat.setPassengerAge(passenger.getPassengerAge());
            bookingSeats.add(bookingSeat);
        }

        booking.setTotalAmount(totalAmount);
        booking.setBookingSeats(bookingSeats);

        Booking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    @Override
    public BookingResponse confirmBooking(String userEmail, Long bookingId) {
        Booking booking = getOwnedBooking(userEmail, bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking is not in a confirmable state");
        }
        if (booking.getLockExpiresAt().isBefore(LocalDateTime.now())) {
            releaseBooking(booking);
            throw new BadRequestException("Seat hold has expired. Please book again.");
        }

        // Simulate payment success - mark booking confirmed, seats booked
        booking.setStatus(BookingStatus.CONFIRMED);
        for (BookingSeat bs : booking.getBookingSeats()) {
            bs.getSeat().setStatus(SeatStatus.BOOKED);
            seatRepository.save(bs.getSeat());
        }

        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }

    @Override
    public BookingResponse cancelBooking(String userEmail, Long bookingId) {
        Booking booking = getOwnedBooking(userEmail, bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        for (BookingSeat bs : booking.getBookingSeats()) {
            bs.getSeat().setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(bs.getSeat());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }

    @Override
    public BookingResponse getByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with PNR: " + pnr));
        return toResponse(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    // ---------- Helper methods ----------

    private Booking getOwnedBooking(String userEmail, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You don't have access to this booking");
        }
        return booking;
    }

    private void releaseBooking(Booking booking) {
        for (BookingSeat bs : booking.getBookingSeats()) {
            bs.getSeat().setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(bs.getSeat());
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BigDecimal calculatePrice(Flight flight, Seat seat) {
        BigDecimal base = flight.getBasePrice();
        if (seat.getSeatClass() == SeatClass.BUSINESS) {
            base = base.multiply(BUSINESS_CLASS_MULTIPLIER);
        }

        // Simple demand-based dynamic pricing: +1% per booked seat on this flight
        long bookedCount = seatRepository.findByFlightId(flight.getId()).stream()
                .filter(s -> s.getStatus() == SeatStatus.BOOKED)
                .count();
        BigDecimal demandMultiplier = BigDecimal.ONE.add(
                new BigDecimal(bookedCount).multiply(new BigDecimal("0.01"))
        );

        return base.multiply(demandMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private String generatePnr() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookingResponse toResponse(Booking booking) {
        Flight flight = booking.getFlight();

        List<BookingSeatResponse> seatResponses = booking.getBookingSeats().stream()
                .map(bs -> new BookingSeatResponse(
                        bs.getSeat().getId(),
                        bs.getSeat().getSeatNumber(),
                        bs.getSeat().getSeatClass().name(),
                        bs.getPassengerName(),
                        bs.getPassengerAge()
                ))
                .toList();

        FlightResponse flightResponse = buildFlightResponse(flight);

        return new BookingResponse(
                booking.getId(),
                booking.getPnr(),
                booking.getStatus(),
                booking.getTotalAmount(),
                booking.getLockExpiresAt(),
                flightResponse,
                seatResponses
        );
    }

    private FlightResponse buildFlightResponse(Flight flight) {
        Route r = flight.getRoute();
        Aircraft a = flight.getAircraft();

        AirportResponse sourceAirport = new AirportResponse(
                r.getSourceAirport().getId(), r.getSourceAirport().getCode(),
                r.getSourceAirport().getName(), r.getSourceAirport().getCity(), r.getSourceAirport().getCountry());
        AirportResponse destAirport = new AirportResponse(
                r.getDestinationAirport().getId(), r.getDestinationAirport().getCode(),
                r.getDestinationAirport().getName(), r.getDestinationAirport().getCity(), r.getDestinationAirport().getCountry());

        RouteResponse routeResponse = new RouteResponse(r.getId(), sourceAirport, destAirport, r.getDistanceKm());
        AircraftResponse aircraftResponse = new AircraftResponse(a.getId(), a.getModel(), a.getTotalSeats(), a.getEconomySeats(), a.getBusinessSeats());

        return new FlightResponse(
                flight.getId(), flight.getFlightNumber(), routeResponse, aircraftResponse,
                flight.getDepartureTime(), flight.getArrivalTime(), flight.getStatus(), flight.getBasePrice()
        );
    }
}