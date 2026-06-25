package com.airline.airline_management_system.service;

import com.airline.airline_management_system.dto.request.RouteRequest;
import com.airline.airline_management_system.dto.response.RouteResponse;

import java.util.List;

public interface RouteService {
    RouteResponse create(RouteRequest request);
    RouteResponse update(Long id, RouteRequest request);
    void delete(Long id);
    RouteResponse getById(Long id);
    List<RouteResponse> getAll();
}