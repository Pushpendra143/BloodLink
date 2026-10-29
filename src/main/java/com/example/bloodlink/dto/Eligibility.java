package com.example.bloodlink.dto;

import java.time.LocalDate;

public record Eligibility(boolean eligible, String reason, LocalDate nextEligibleDate) {
}
