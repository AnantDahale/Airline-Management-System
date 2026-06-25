package com.airline.airline_management_system.repository;

import com.airline.airline_management_system.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}