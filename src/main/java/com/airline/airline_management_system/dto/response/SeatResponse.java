package com.airline.airline_management_system.dto.response;

import com.airline.airline_management_system.enums.SeatClass;
import com.airline.airline_management_system.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private SeatClass seatClass;
    private SeatStatus status;
}