package com.airline.airline_management_system.dto.response;

import com.airline.airline_management_system.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String pnr;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime lockExpiresAt;
    private FlightResponse flight;
    private List<BookingSeatResponse> seats;
}