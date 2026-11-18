package com.kushagra.hospital.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateAndSlotAndStatus(
            Long doctorId, LocalDate appointmentDate, LocalTime slot, AppointmentStatus status);

    @Query("select a.slot from Appointment a where a.doctor.id = :doctorId "
         + "and a.appointmentDate = :appointmentDate "
         + "and a.status = com.kushagra.hospital.appointment.AppointmentStatus.BOOKED")
    List<LocalTime> findBookedSlots(@Param("doctorId") Long doctorId,
                                    @Param("appointmentDate") LocalDate appointmentDate);
}