package com.airline.airline_management_system.service.impl;

import com.airline.airline_management_system.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendBookingConfirmation(String toEmail, String passengerName, String pnr, String flightNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Booking Confirmed - PNR " + pnr);
        message.setText("Dear " + passengerName + ",\n\nYour booking for flight " + flightNumber +
                " is confirmed.\nPNR: " + pnr + "\n\nThank you for flying with us!");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendFlightStatusUpdate(String toEmail, String flightNumber, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Flight " + flightNumber + " Status Update");
        message.setText("Flight " + flightNumber + " status has changed to: " + status);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}