package com.airline.airline_management_system.service.impl;

import com.airline.airline_management_system.dto.request.RouteRequest;
import com.airline.airline_management_system.dto.response.AirportResponse;
import com.airline.airline_management_system.dto.response.RouteResponse;
import com.airline.airline_management_system.entity.Airport;
import com.airline.airline_management_system.entity.Route;
import com.airline.airline_management_system.exception.BadRequestException;
import com.airline.airline_management_system.exception.ResourceNotFoundException;
import com.airline.airline_management_system.repository.AirportRepository;
import com.airline.airline_management_system.repository.RouteRepository;
import com.airline.airline_management_system.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final AirportRepository airportRepository;

    @Override
    public RouteResponse create(RouteRequest request) {
        if (request.getSourceAirportId().equals(request.getDestinationAirportId())) {
            throw new BadRequestException("Source and destination airports cannot be the same");
        }

        Airport source = airportRepository.findById(request.getSourceAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Source airport not found"));
        Airport destination = airportRepository.findById(request.getDestinationAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination airport not found"));

        Route route = new Route();
        route.setSourceAirport(source);
        route.setDestinationAirport(destination);
        route.setDistanceKm(request.getDistanceKm());

        return toResponse(routeRepository.save(route));
    }

    @Override
    public RouteResponse update(Long id, RouteRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        Airport source = airportRepository.findById(request.getSourceAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Source airport not found"));
        Airport destination = airportRepository.findById(request.getDestinationAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination airport not found"));

        route.setSourceAirport(source);
        route.setDestinationAirport(destination);
        route.setDistanceKm(request.getDistanceKm());

        return toResponse(routeRepository.save(route));
    }

    @Override
    public void delete(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        routeRepository.delete(route);
    }

    @Override
    public RouteResponse getById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return toResponse(route);
    }

    @Override
    public List<RouteResponse> getAll() {
        return routeRepository.findAll().stream().map(this::toResponse).toList();
    }

    private RouteResponse toResponse(Route route) {
        return new RouteResponse(
                route.getId(),
                toAirportResponse(route.getSourceAirport()),
                toAirportResponse(route.getDestinationAirport()),
                route.getDistanceKm()
        );
    }

    private AirportResponse toAirportResponse(Airport airport) {
        return new AirportResponse(
                airport.getId(), airport.getCode(), airport.getName(), airport.getCity(), airport.getCountry()
        );
    }
}