package com.example.bloodlink.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bloodlink.dto.DonationForm;
import com.example.bloodlink.dto.Eligibility;
import com.example.bloodlink.exception.BusinessRuleException;
import com.example.bloodlink.model.BloodRequest;
import com.example.bloodlink.model.Donation;
import com.example.bloodlink.model.Donor;
import com.example.bloodlink.model.RequestStatus;
import com.example.bloodlink.repository.DonationRepository;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonorService donorService;
    private final BloodRequestService requestService;
    private final StockService stockService;

    public DonationService(DonationRepository donationRepository,
                           DonorService donorService,
                           BloodRequestService requestService,
                           StockService stockService) {
        this.donationRepository = donationRepository;
        this.donorService = donorService;
        this.requestService = requestService;
        this.stockService = stockService;
    }

    @Transactional
    public Donation record(DonationForm form) {
        Donor donor = donorService.getById(form.donorId());

        Eligibility eligibility = donorService.checkEligibility(donor);
        if (!eligibility.eligible()) {
            throw new BusinessRuleException("Donation not allowed: " + eligibility.reason());
        }

        BloodRequest request = null;
        if (form.requestId() != null) {
            request = requestService.getById(form.requestId());
            requestService.ensureOpen(request);

            if (!request.getBloodGroup().canReceiveFrom(donor.getBloodGroup())) {
                throw new BusinessRuleException(donor.getBloodGroup().getLabel() + " blood cannot be given to a "
                        + request.getBloodGroup().getLabel() + " patient");
            }

            request.setUnitsFulfilled(request.getUnitsFulfilled() + 1);
            if (request.getUnitsRemaining() <= 0) {
                request.setStatus(RequestStatus.FULFILLED);
            }
            requestService.save(request);
        } else {
            // no particular request, so it goes into the bank
            stockService.add(donor.getBloodGroup(), 1);
        }

        donorService.markDonatedToday(donor);

        Donation donation = new Donation();
        donation.setDonor(donor);
        donation.setRequest(request);
        donation.setDonatedOn(LocalDate.now());
        return donationRepository.save(donation);
    }

    public List<Donation> getAll() {
        return donationRepository.findAll();
    }

    public List<Donation> getForDonor(Long donorId) {
        donorService.getById(donorId); // 404 if the donor does not exist
        return donationRepository.findByDonorIdOrderByDonatedOnDescIdDesc(donorId);
    }
}
