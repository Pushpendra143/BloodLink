package com.example.bloodlink.dto;

import java.time.LocalDate;

import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.Urgency;

import jakarta.validation.constraints.*;

public record BloodRequestForm(

        @NotNull(message = "Hospital ID is required")
        Long hospitalId,

        @NotBlank(message = "Patient name is required")
        String patientName,

        @NotNull(message = "Blood group is required")
        BloodGroup bloodGroup,

        @NotNull(message = "Units needed is required")
        @Min(value = 1, message = "At least 1 unit must be requested")
        @Max(value = 10, message = "A single request can ask for at most 10 units")
        Integer unitsNeeded,

        // optional, NORMAL if left out
        Urgency urgency,

        @NotNull(message = "Required-by date is required")
        @FutureOrPresent(message = "Required-by date cannot be in the past")
        LocalDate requiredBy) {
}
