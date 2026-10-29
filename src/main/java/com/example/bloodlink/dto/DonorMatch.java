package com.example.bloodlink.dto;

import com.example.bloodlink.model.BloodGroup;

public record DonorMatch(Long donorId, String name, BloodGroup bloodGroup, String phone, boolean exactMatch) {
}
