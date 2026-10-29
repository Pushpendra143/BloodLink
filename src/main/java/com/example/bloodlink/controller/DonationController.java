package com.example.bloodlink.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.bloodlink.dto.DonationForm;
import com.example.bloodlink.model.Donation;
import com.example.bloodlink.service.DonationService;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Donation record(@Valid @RequestBody DonationForm form) {
        return donationService.record(form);
    }

    @GetMapping
    public List<Donation> list() {
        return donationService.getAll();
    }
}
