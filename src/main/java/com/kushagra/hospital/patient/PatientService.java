package com.kushagra.hospital.patient;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public List<Patient> findAll() { return repository.findAll(); }

    public Patient findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient " + id + " not found"));
    }

    public Patient create(Patient patient) { return repository.save(patient); }

    public Patient update(Long id, Patient updated) {
        Patient patient = findById(id);
        patient.setName(updated.getName());
        patient.setAge(updated.getAge());
        patient.setGender(updated.getGender());
        patient.setPhone(updated.getPhone());
        return repository.save(patient);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}