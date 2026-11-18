package com.kushagra.hospital.appointment;

import com.kushagra.hospital.doctor.Doctor;
import com.kushagra.hospital.doctor.DoctorService;
import com.kushagra.hospital.patient.Patient;
import com.kushagra.hospital.patient.PatientService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppointmentService {

    // 30-minute slots from 09:00 to 16:30
    public static final List<LocalTime> SLOTS = IntStream.range(0, 16)
            .mapToObj(i -> LocalTime.of(9, 0).plusMinutes(30L * i))
            .toList();

    private final AppointmentRepository appointments;
    private final PatientService patients;
    private final DoctorService doctors;

    public AppointmentService(AppointmentRepository appointments,
                              PatientService patients, DoctorService doctors) {
        this.appointments = appointments;
        this.patients = patients;
        this.doctors = doctors;
    }

    public List<Appointment> findAll() { return appointments.findAll(); }

    public Appointment findById(Long id) {
        return appointments.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment " + id + " not found"));
    }

    @Transactional
    public Appointment book(AppointmentRequest request) {
        Patient patient = patients.findById(request.patientId());
        Doctor doctor = doctors.findById(request.doctorId());

        if (request.appointmentDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "appointmentDate cannot be in the past");
        }
        if (!SLOTS.contains(request.slot())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "slot must be a 30-minute slot between 09:00 and 16:30");
        }
        if (appointments.existsByDoctorIdAndAppointmentDateAndSlotAndStatus(
                doctor.getId(), request.appointmentDate(), request.slot(), AppointmentStatus.BOOKED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Doctor " + doctor.getId() + " is already booked at that time");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setSlot(request.slot());
        appointment.setReason(request.reason());
        appointment.setStatus(AppointmentStatus.BOOKED);
        return appointments.save(appointment);
    }

    @Transactional
    public Appointment cancel(Long id) {
        Appointment appointment = findById(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment is already cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointments.save(appointment);
    }

    public List<LocalTime> availableSlots(Long doctorId, LocalDate date) {
        doctors.findById(doctorId);
        List<LocalTime> booked = appointments.findBookedSlots(doctorId, date);
        return SLOTS.stream().filter(slot -> !booked.contains(slot)).toList();
    }
}