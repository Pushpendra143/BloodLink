package com.example.bloodlink.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.bloodlink.dto.BloodRequestForm;
import com.example.bloodlink.dto.DonorMatch;
import com.example.bloodlink.model.BloodRequest;
import com.example.bloodlink.model.RequestStatus;
import com.example.bloodlink.service.BloodRequestService;
import com.example.bloodlink.service.MatchingService;

@RestController
@RequestMapping("/api/requests")
public class BloodRequestController {

    private final BloodRequestService requestService;
    private final MatchingService matchingService;

    public BloodRequestController(BloodRequestService requestService, MatchingService matchingService) {
        this.requestService = requestService;
        this.matchingService = matchingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BloodRequest create(@Valid @RequestBody BloodRequestForm form) {
        return requestService.create(form);
    }

    // /api/requests?status=OPEN
    @GetMapping
    public List<BloodRequest> list(@RequestParam(required = false) RequestStatus status) {
        return requestService.list(status);
    }

    @GetMapping("/{id}")
    public BloodRequest get(@PathVariable Long id) {
        return requestService.getById(id);
    }

    // who can we call for this request?
    @GetMapping("/{id}/matches")
    public List<DonorMatch> matches(@PathVariable Long id) {
        return matchingService.findMatches(id);
    }

    @PostMapping("/{id}/fulfill-from-stock")
    public BloodRequest fulfillFromStock(@PathVariable Long id) {
        return requestService.fulfillFromStock(id);
    }

    @PostMapping("/{id}/cancel")
    public BloodRequest cancel(@PathVariable Long id) {
        return requestService.cancel(id);
    }
}
