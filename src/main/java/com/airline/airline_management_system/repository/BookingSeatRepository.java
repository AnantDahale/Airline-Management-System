package com.airline.airline_management_system.repository;

import com.airline.airline_management_system.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
}