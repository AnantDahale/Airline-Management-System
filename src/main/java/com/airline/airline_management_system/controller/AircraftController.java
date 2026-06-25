package com.airline.airline_management_system.controller;

import com.airline.airline_management_system.dto.request.AircraftRequest;
import com.airline.airline_management_system.dto.response.AircraftResponse;
import com.airline.airline_management_system.service.AircraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aircraft")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @PostMapping
    public ResponseEntity<AircraftResponse> create(@Valid @RequestBody AircraftRequest request) {
        return ResponseEntity.ok(aircraftService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponse> update(@PathVariable Long id, @Valid @RequestBody AircraftRequest request) {
        return ResponseEntity.ok(aircraftService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        aircraftService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(aircraftService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AircraftResponse>> getAll() {
        return ResponseEntity.ok(aircraftService.getAll());
    }
}