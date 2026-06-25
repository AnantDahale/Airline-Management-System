package com.airline.airline_management_system.service;

import com.airline.airline_management_system.dto.request.FlightRequest;
import com.airline.airline_management_system.dto.response.FlightResponse;
import com.airline.airline_management_system.enums.FlightStatus;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    FlightResponse create(FlightRequest request);
    FlightResponse update(Long id, FlightRequest request);
    FlightResponse updateStatus(Long id, FlightStatus status);
    void delete(Long id);
    FlightResponse getById(Long id);
    List<FlightResponse> getAll();
    List<FlightResponse> search(String source, String destination, LocalDate date);
}