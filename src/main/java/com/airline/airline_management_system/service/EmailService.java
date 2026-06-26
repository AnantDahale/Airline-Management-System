package com.airline.airline_management_system.service;

public interface EmailService {
    void sendBookingConfirmation(String toEmail, String passengerName, String pnr, String flightNumber);
    void sendFlightStatusUpdate(String toEmail, String flightNumber, String status);
}