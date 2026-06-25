package com.airline.airline_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AirportRequest {

    @NotBlank(message = "Airport code is required")
    @Size(min = 3, max = 10, message = "Code must be 3-10 characters")
    private String code;

    @NotBlank(message = "Airport name is required")
    private String name;

    private String city;
    private String country;
}