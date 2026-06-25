package com.airline.airline_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeatResponse {
    private Long seatId;
    private String seatNumber;
    private String seatClass;
    private String passengerName;
    private Integer passengerAge;
}