package com.airline.airline_management_system.service.impl;

import com.airline.airline_management_system.dto.request.AirportRequest;
import com.airline.airline_management_system.dto.response.AirportResponse;
import com.airline.airline_management_system.entity.Airport;
import com.airline.airline_management_system.exception.BadRequestException;
import com.airline.airline_management_system.exception.ResourceNotFoundException;
import com.airline.airline_management_system.repository.AirportRepository;
import com.airline.airline_management_system.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;

    @Override
    public AirportResponse create(AirportRequest request) {
        if (airportRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Airport code already exists");
        }

        Airport airport = new Airport();
        airport.setCode(request.getCode().toUpperCase());
        airport.setName(request.getName());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());

        Airport saved = airportRepository.save(airport);
        return toResponse(saved);
    }

    @Override
    public AirportResponse update(Long id, AirportRequest request) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found with id: " + id));

        airport.setCode(request.getCode().toUpperCase());
        airport.setName(request.getName());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());

        Airport updated = airportRepository.save(airport);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found with id: " + id));
        airportRepository.delete(airport);
    }

    @Override
    public AirportResponse getById(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found with id: " + id));
        return toResponse(airport);
    }

    @Override
    public List<AirportResponse> getAll() {
        return airportRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private AirportResponse toResponse(Airport airport) {
        return new AirportResponse(
                airport.getId(),
                airport.getCode(),
                airport.getName(),
                airport.getCity(),
                airport.getCountry()
        );
    }
}