package com.example.bloodlink.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.Donor;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    boolean existsByEmailIgnoreCase(String email);

    long countByActiveTrue();

    List<Donor> findByCityIgnoreCaseAndBloodGroupInAndActiveTrue(String city, Collection<BloodGroup> groups);
}
