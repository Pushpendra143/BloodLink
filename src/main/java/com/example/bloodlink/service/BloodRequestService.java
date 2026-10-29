package com.example.bloodlink.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bloodlink.dto.BloodRequestForm;
import com.example.bloodlink.exception.BusinessRuleException;
import com.example.bloodlink.exception.ResourceNotFoundException;
import com.example.bloodlink.model.BloodRequest;
import com.example.bloodlink.model.Hospital;
import com.example.bloodlink.model.RequestStatus;
import com.example.bloodlink.model.Urgency;
import com.example.bloodlink.repository.BloodRequestRepository;

@Service
public class BloodRequestService {

    private final BloodRequestRepository requestRepository;
    private final HospitalService hospitalService;
    private final StockService stockService;

    public BloodRequestService(BloodRequestRepository requestRepository,
                               HospitalService hospitalService,
                               StockService stockService) {
        this.requestRepository = requestRepository;
        this.hospitalService = hospitalService;
        this.stockService = stockService;
    }

    public BloodRequest create(BloodRequestForm form) {
        Hospital hospital = hospitalService.getById(form.hospitalId());

        BloodRequest request = new BloodRequest();
        request.setHospital(hospital);
        request.setPatientName(form.patientName().trim());
        request.setBloodGroup(form.bloodGroup());
        request.setUnitsNeeded(form.unitsNeeded());
        request.setUrgency(form.urgency() == null ? Urgency.NORMAL : form.urgency());
        request.setRequiredBy(form.requiredBy());
        return requestRepository.save(request);
    }

    public BloodRequest getById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request with ID " + id + " was not found"));
    }

    // most critical first, then the one that is needed earliest
    public List<BloodRequest> list(RequestStatus status) {
        List<BloodRequest> all = (status == null) ? requestRepository.findAll() : requestRepository.findByStatus(status);
        return all.stream()
                .sorted(Comparator.comparing(BloodRequest::getUrgency, Comparator.reverseOrder())
                        .thenComparing(BloodRequest::getRequiredBy))
                .toList();
    }

    public BloodRequest save(BloodRequest request) {
        return requestRepository.save(request);
    }

    public BloodRequest cancel(Long id) {
        BloodRequest request = getById(id);
        ensureOpen(request);
        request.setStatus(RequestStatus.CANCELLED);
        return requestRepository.save(request);
    }

    // uses units of the exact same blood group from the hospital stock
    @Transactional
    public BloodRequest fulfillFromStock(Long id) {
        BloodRequest request = getById(id);
        ensureOpen(request);

        int available = stockService.get(request.getBloodGroup()).getUnitsAvailable();
        if (available == 0) {
            throw new BusinessRuleException("No " + request.getBloodGroup().getLabel() + " units in stock right now");
        }

        int toTake = Math.min(available, request.getUnitsRemaining());
        stockService.take(request.getBloodGroup(), toTake);

        request.setUnitsFulfilled(request.getUnitsFulfilled() + toTake);
        if (request.getUnitsRemaining() <= 0) {
            request.setStatus(RequestStatus.FULFILLED);
        }
        return requestRepository.save(request);
    }

    public void ensureOpen(BloodRequest request) {
        if (request.getStatus() != RequestStatus.OPEN) {
            throw new BusinessRuleException("Request #" + request.getId() + " is already " + request.getStatus());
        }
    }
}
