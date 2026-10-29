package com.example.bloodlink.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.bloodlink.dto.Summary;
import com.example.bloodlink.model.BloodStock;
import com.example.bloodlink.model.RequestStatus;
import com.example.bloodlink.model.Urgency;
import com.example.bloodlink.repository.BloodRequestRepository;
import com.example.bloodlink.repository.DonationRepository;
import com.example.bloodlink.repository.DonorRepository;

@Service
public class ReportService {

    private final DonorRepository donorRepository;
    private final BloodRequestRepository requestRepository;
    private final DonationRepository donationRepository;
    private final DonorService donorService;
    private final StockService stockService;

    public ReportService(DonorRepository donorRepository,
                         BloodRequestRepository requestRepository,
                         DonationRepository donationRepository,
                         DonorService donorService,
                         StockService stockService) {
        this.donorRepository = donorRepository;
        this.requestRepository = requestRepository;
        this.donationRepository = donationRepository;
        this.donorService = donorService;
        this.stockService = stockService;
    }

    public Summary summary() {
        long eligible = donorRepository.findAll().stream()
                .filter(d -> donorService.checkEligibility(d).eligible())
                .count();

        Map<String, Integer> stock = new LinkedHashMap<>();
        for (BloodStock s : stockService.getAll()) {
            stock.put(s.getBloodGroup().getLabel(), s.getUnitsAvailable());
        }

        return new Summary(
                donorRepository.count(),
                donorRepository.countByActiveTrue(),
                eligible,
                requestRepository.countByStatus(RequestStatus.OPEN),
                requestRepository.countByStatusAndUrgency(RequestStatus.OPEN, Urgency.CRITICAL),
                donationRepository.count(),
                stock);
    }
}
