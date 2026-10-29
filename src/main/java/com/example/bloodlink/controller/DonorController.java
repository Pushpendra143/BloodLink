package com.example.bloodlink.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.bloodlink.dto.Eligibility;
import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.Donation;
import com.example.bloodlink.model.Donor;
import com.example.bloodlink.service.DonationService;
import com.example.bloodlink.service.DonorService;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;
    private final DonationService donationService;

    public DonorController(DonorService donorService, DonationService donationService) {
        this.donorService = donorService;
        this.donationService = donationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Donor register(@Valid @RequestBody Donor donor) {
        return donorService.register(donor);
    }

    // optional filters: /api/donors?bloodGroup=O%2B&city=Ahmedabad
    @GetMapping
    public List<Donor> list(@RequestParam(required = false) BloodGroup bloodGroup,
                            @RequestParam(required = false) String city) {
        return donorService.search(bloodGroup, city);
    }

    @GetMapping("/{id}")
    public Donor get(@PathVariable Long id) {
        return donorService.getById(id);
    }

    @PutMapping("/{id}")
    public Donor update(@PathVariable Long id, @Valid @RequestBody Donor donor) {
        return donorService.update(id, donor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        donorService.deactivate(id);
    }

    @GetMapping("/{id}/eligibility")
    public Eligibility eligibility(@PathVariable Long id) {
        return donorService.checkEligibility(donorService.getById(id));
    }

    @GetMapping("/{id}/donations")
    public List<Donation> history(@PathVariable Long id) {
        return donationService.getForDonor(id);
    }
}
