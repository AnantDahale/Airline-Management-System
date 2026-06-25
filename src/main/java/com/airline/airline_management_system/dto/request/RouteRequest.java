package com.airline.airline_management_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteRequest {

    @NotNull(message = "Source airport ID is required")
    private Long sourceAirportId;

    @NotNull(message = "Destination airport ID is required")
    private Long destinationAirportId;

    private Integer distanceKm;
}