package com.example.bloodlink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bloodlink.exception.DuplicateResourceException;
import com.example.bloodlink.exception.ResourceNotFoundException;
import com.example.bloodlink.model.Hospital;
import com.example.bloodlink.repository.HospitalRepository;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public Hospital register(Hospital hospital) {
        if (hospitalRepository.existsByRegistrationNoIgnoreCase(hospital.getRegistrationNo())) {
            throw new DuplicateResourceException(
                    "Hospital with registration number " + hospital.getRegistrationNo() + " already exists");
        }
        hospital.setId(null);
        return hospitalRepository.save(hospital);
    }

    public List<Hospital> getAll() {
        return hospitalRepository.findAll();
    }

    public Hospital getById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital with ID " + id + " was not found"));
    }
}
