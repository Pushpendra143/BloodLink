package com.example.bloodlink.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bloodlink.model.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    boolean existsByRegistrationNoIgnoreCase(String registrationNo);
}
