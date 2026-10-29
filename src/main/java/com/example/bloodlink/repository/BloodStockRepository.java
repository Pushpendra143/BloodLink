package com.example.bloodlink.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.BloodStock;

public interface BloodStockRepository extends JpaRepository<BloodStock, BloodGroup> {
}
