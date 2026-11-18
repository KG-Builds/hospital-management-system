package com.kushagra.hospital.appointment;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) { this.service = service; }

    @GetMapping
    public List<Appointment> all() { return service.findAll(); }

    @GetMapping("/available-slots")
    public List<LocalTime> availableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.availableSlots(doctorId, date);
    }

    @GetMapping("/{id}")
    public Appointment one(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment book(@Valid @RequestBody AppointmentRequest request) {
        return service.book(request);
    }

    @PutMapping("/{id}/cancel")
    public Appointment cancel(@PathVariable Long id) { return service.cancel(id); }
}