package com.example.bloodlink.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.bloodlink.dto.Eligibility;
import com.example.bloodlink.exception.BusinessRuleException;
import com.example.bloodlink.exception.DuplicateResourceException;
import com.example.bloodlink.exception.ResourceNotFoundException;
import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.Donor;
import com.example.bloodlink.repository.DonorRepository;

@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final int gapDays;
    private final int minAge;
    private final int maxAge;

    public DonorService(DonorRepository donorRepository,
                        @Value("${bloodlink.donation-gap-days:90}") int gapDays,
                        @Value("${bloodlink.min-donor-age:18}") int minAge,
                        @Value("${bloodlink.max-donor-age:65}") int maxAge) {
        this.donorRepository = donorRepository;
        this.gapDays = gapDays;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public Donor register(Donor donor) {
        checkAge(donor.getDateOfBirth());
        if (donorRepository.existsByEmailIgnoreCase(donor.getEmail())) {
            throw new DuplicateResourceException(
                    "A donor with email " + donor.getEmail() + " is already registered");
        }
        // never trust these from the client
        donor.setId(null);
        donor.setEmail(donor.getEmail().trim().toLowerCase());
        donor.setActive(true);
        donor.setRegisteredOn(LocalDate.now());
        return donorRepository.save(donor);
    }

    public Donor update(Long id, Donor incoming) {
        Donor donor = getById(id);
        checkAge(incoming.getDateOfBirth());

        String newEmail = incoming.getEmail().trim().toLowerCase();
        if (!donor.getEmail().equalsIgnoreCase(newEmail) && donorRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new DuplicateResourceException("Email " + newEmail + " is used by another donor");
        }

        donor.setFullName(incoming.getFullName());
        donor.setEmail(newEmail);
        donor.setPhone(incoming.getPhone());
        donor.setBloodGroup(incoming.getBloodGroup());
        donor.setCity(incoming.getCity());
        donor.setDateOfBirth(incoming.getDateOfBirth());
        donor.setLastDonationDate(incoming.getLastDonationDate());
        return donorRepository.save(donor);
    }

    // we never delete donors because their donation history must stay
    public void deactivate(Long id) {
        Donor donor = getById(id);
        donor.setActive(false);
        donorRepository.save(donor);
    }

    public Donor getById(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor with ID " + id + " was not found"));
    }

    public List<Donor> search(BloodGroup group, String city) {
        return donorRepository.findAll().stream()
                .filter(d -> group == null || d.getBloodGroup() == group)
                .filter(d -> city == null || city.isBlank() || d.getCity().equalsIgnoreCase(city.trim()))
                .toList();
    }

    public void markDonatedToday(Donor donor) {
        donor.setLastDonationDate(LocalDate.now());
        donorRepository.save(donor);
    }

    public int ageOf(Donor donor) {
        return Period.between(donor.getDateOfBirth(), LocalDate.now()).getYears();
    }

    public Eligibility checkEligibility(Donor donor) {
        if (!donor.isActive()) {
            return new Eligibility(false, "Donor profile is deactivated", null);
        }

        int age = ageOf(donor);
        if (age < minAge || age > maxAge) {
            return new Eligibility(false,
                    "Donors must be between " + minAge + " and " + maxAge + " years old", null);
        }

        LocalDate last = donor.getLastDonationDate();
        if (last != null) {
            LocalDate next = last.plusDays(gapDays);
            if (next.isAfter(LocalDate.now())) {
                return new Eligibility(false,
                        "Last donation was on " + last + ", a gap of " + gapDays + " days is required", next);
            }
        }
        return new Eligibility(true, "Eligible to donate", null);
    }

    private void checkAge(LocalDate dob) {
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < minAge || age > maxAge) {
            throw new BusinessRuleException("Donor must be between " + minAge + " and "
                    + maxAge + " years old (age calculated: " + age + ")");
        }
    }
}
