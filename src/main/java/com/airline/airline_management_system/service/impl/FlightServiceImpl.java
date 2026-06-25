package com.airline.airline_management_system.service.impl;

import com.airline.airline_management_system.dto.request.FlightRequest;
import com.airline.airline_management_system.dto.response.*;
import com.airline.airline_management_system.entity.Aircraft;
import com.airline.airline_management_system.entity.Flight;
import com.airline.airline_management_system.entity.Route;
import com.airline.airline_management_system.entity.Seat;
import com.airline.airline_management_system.enums.FlightStatus;
import com.airline.airline_management_system.enums.SeatClass;
import com.airline.airline_management_system.exception.BadRequestException;
import com.airline.airline_management_system.exception.ResourceNotFoundException;
import com.airline.airline_management_system.repository.AircraftRepository;
import com.airline.airline_management_system.repository.FlightRepository;
import com.airline.airline_management_system.repository.RouteRepository;
import com.airline.airline_management_system.repository.SeatRepository;
import com.airline.airline_management_system.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final RouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;
    private final SeatRepository seatRepository;

    @Override
    public FlightResponse create(FlightRequest request) {
        if (flightRepository.existsByFlightNumber(request.getFlightNumber())) {
            throw new BadRequestException("Flight number already exists");
        }
        if (!request.getArrivalTime().isAfter(request.getDepartureTime())) {
            throw new BadRequestException("Arrival time must be after departure time");
        }

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found"));

        Flight flight = new Flight();
        flight.setFlightNumber(request.getFlightNumber().toUpperCase());
        flight.setRoute(route);
        flight.setAircraft(aircraft);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setBasePrice(request.getBasePrice());
        flight.setStatus(FlightStatus.SCHEDULED);

        Flight saved = flightRepository.save(flight);
        generateSeats(saved, aircraft);

        return toResponse(saved);
    }

    @Override
    public FlightResponse update(Long id, FlightRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        if (!request.getArrivalTime().isAfter(request.getDepartureTime())) {
            throw new BadRequestException("Arrival time must be after departure time");
        }

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found"));

        flight.setFlightNumber(request.getFlightNumber().toUpperCase());
        flight.setRoute(route);
        flight.setAircraft(aircraft);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setBasePrice(request.getBasePrice());

        return toResponse(flightRepository.save(flight));
    }

    @Override
    public FlightResponse updateStatus(Long id, FlightStatus status) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        flight.setStatus(status);
        return toResponse(flightRepository.save(flight));
    }

    @Override
    public void delete(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        flightRepository.delete(flight);
    }

    @Override
    public FlightResponse getById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        return toResponse(flight);
    }

    @Override
    public List<FlightResponse> getAll() {
        return flightRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<FlightResponse> search(String source, String destination, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return flightRepository.searchFlights(source, destination, startOfDay, endOfDay)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private void generateSeats(Flight flight, Aircraft aircraft) {
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= aircraft.getBusinessSeats(); i++) {
            Seat seat = new Seat();
            seat.setFlight(flight);
            seat.setSeatNumber("B" + i);
            seat.setSeatClass(SeatClass.BUSINESS);
            seats.add(seat);
        }

        for (int i = 1; i <= aircraft.getEconomySeats(); i++) {
            Seat seat = new Seat();
            seat.setFlight(flight);
            seat.setSeatNumber("E" + i);
            seat.setSeatClass(SeatClass.ECONOMY);
            seats.add(seat);
        }

        seatRepository.saveAll(seats);
    }

    private FlightResponse toResponse(Flight flight) {
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