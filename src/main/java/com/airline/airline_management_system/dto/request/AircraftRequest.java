package com.airline.airline_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AircraftRequest {

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Economy seats is required")
    @Positive(message = "Economy seats must be positive")
    private Integer economySeats;

    @NotNull(message = "Business seats is required")
    @Positive(message = "Business seats must be positive")
    private Integer businessSeats;
}