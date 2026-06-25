package com.airline.airline_management_system.service.impl;

import com.airline.airline_management_system.dto.request.AircraftRequest;
import com.airline.airline_management_system.dto.response.AircraftResponse;
import com.airline.airline_management_system.entity.Aircraft;
import com.airline.airline_management_system.exception.ResourceNotFoundException;
import com.airline.airline_management_system.repository.AircraftRepository;
import com.airline.airline_management_system.service.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;

    @Override
    public AircraftResponse create(AircraftRequest request) {
        Aircraft aircraft = new Aircraft();
        aircraft.setModel(request.getModel());
        aircraft.setEconomySeats(request.getEconomySeats());
        aircraft.setBusinessSeats(request.getBusinessSeats());
        aircraft.setTotalSeats(request.getEconomySeats() + request.getBusinessSeats());

        Aircraft saved = aircraftRepository.save(aircraft);
        return toResponse(saved);
    }

    @Override
    public AircraftResponse update(Long id, AircraftRequest request) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id: " + id));

        aircraft.setModel(request.getModel());
        aircraft.setEconomySeats(request.getEconomySeats());
        aircraft.setBusinessSeats(request.getBusinessSeats());
        aircraft.setTotalSeats(request.getEconomySeats() + request.getBusinessSeats());

        Aircraft updated = aircraftRepository.save(aircraft);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id: " + id));
        aircraftRepository.delete(aircraft);
    }

    @Override
    public AircraftResponse getById(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id: " + id));
        return toResponse(aircraft);
    }

    @Override
    public List<AircraftResponse> getAll() {
        return aircraftRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private AircraftResponse toResponse(Aircraft aircraft) {
        return new AircraftResponse(
                aircraft.getId(),
                aircraft.getModel(),
                aircraft.getTotalSeats(),
                aircraft.getEconomySeats(),
                aircraft.getBusinessSeats()
        );
    }
}