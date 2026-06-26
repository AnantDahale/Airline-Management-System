package com.airline.airline_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Long totalBookings;
    private Long confirmedBookings;
    private Long cancelledBookings;
    private BigDecimal totalRevenue;
    private Long totalFlights;
    private Long totalUsers;
}