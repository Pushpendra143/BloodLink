package com.example.bloodlink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bloodlink.model.BloodRequest;
import com.example.bloodlink.model.RequestStatus;
import com.example.bloodlink.model.Urgency;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findByStatus(RequestStatus status);

    long countByStatus(RequestStatus status);

    long countByStatusAndUrgency(RequestStatus status, Urgency urgency);
}
