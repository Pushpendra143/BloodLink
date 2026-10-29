package com.example.bloodlink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bloodlink.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByDonorIdOrderByDonatedOnDescIdDesc(Long donorId);
}
