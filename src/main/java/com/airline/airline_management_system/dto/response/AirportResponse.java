package com.airline.airline_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirportResponse {
    private Long id;
    private String code;
    private String name;
    private String city;
    private String country;
}