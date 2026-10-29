package com.example.bloodlink.dto;

import jakarta.validation.constraints.NotNull;

// requestId is optional: leave it out to put the blood into general stock
public record DonationForm(

        @NotNull(message = "Donor ID is required")
        Long donorId,

        Long requestId) {
}
