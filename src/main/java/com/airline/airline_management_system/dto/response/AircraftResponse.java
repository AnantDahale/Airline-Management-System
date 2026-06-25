package com.airline.airline_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AircraftResponse {
    private Long id;
    private String model;
    private Integer totalSeats;
    private Integer economySeats;
    private Integer businessSeats;
}