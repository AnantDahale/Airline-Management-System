package com.airline.airline_management_system.repository;

import com.airline.airline_management_system.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
}