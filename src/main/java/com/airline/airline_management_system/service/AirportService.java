package com.airline.airline_management_system.service;

import com.airline.airline_management_system.dto.request.AirportRequest;
import com.airline.airline_management_system.dto.response.AirportResponse;

import java.util.List;

public interface AirportService {
    AirportResponse create(AirportRequest request);
    AirportResponse update(Long id, AirportRequest request);
    void delete(Long id);
    AirportResponse getById(Long id);
    List<AirportResponse> getAll();
}