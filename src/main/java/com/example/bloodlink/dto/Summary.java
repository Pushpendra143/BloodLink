package com.example.bloodlink.dto;

import java.util.Map;

public record Summary(
        long totalDonors,
        long activeDonors,
        long eligibleToday,
        long openRequests,
        long criticalOpenRequests,
        long totalDonations,
        Map<String, Integer> stock) {
}
