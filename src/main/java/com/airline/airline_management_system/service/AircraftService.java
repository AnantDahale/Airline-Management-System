package com.airline.airline_management_system.service;

import com.airline.airline_management_system.dto.request.AircraftRequest;
import com.airline.airline_management_system.dto.response.AircraftResponse;

import java.util.List;

public interface AircraftService {
    AircraftResponse create(AircraftRequest request);
    AircraftResponse update(Long id, AircraftRequest request);
    void delete(Long id);
    AircraftResponse getById(Long id);
    List<AircraftResponse> getAll();
}