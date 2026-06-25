package com.airline.airline_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {
    private Long id;
    private AirportResponse sourceAirport;
    private AirportResponse destinationAirport;
    private Integer distanceKm;
}