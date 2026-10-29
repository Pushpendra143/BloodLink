package com.example.bloodlink.soap;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.bloodlink.dto.Eligibility;
import com.example.bloodlink.exception.ResourceNotFoundException;
import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.BloodStock;
import com.example.bloodlink.model.Donor;
import com.example.bloodlink.service.DonorService;
import com.example.bloodlink.service.StockService;
import com.example.bloodlink.soap.ws.CheckEligibilityRequest;
import com.example.bloodlink.soap.ws.CheckEligibilityResponse;
import com.example.bloodlink.soap.ws.CheckStockRequest;
import com.example.bloodlink.soap.ws.CheckStockResponse;

/*
 * SOAP side of BloodLinkD. Other systems (a hospital's own software, another
 * blood bank) can call these two operations without knowing anything about
 * our REST API. The contract is in xsd/bloodlink.xsd, WSDL is generated at
 * /ws/bloodlink.wsdl
 */
@Endpoint
public class BloodLinkEndpoint {

    private static final String NAMESPACE = "http://bloodlink.example.com/soap";

    private final StockService stockService;
    private final DonorService donorService;

    public BloodLinkEndpoint(StockService stockService, DonorService donorService) {
        this.stockService = stockService;
        this.donorService = donorService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CheckStockRequest")
    @ResponsePayload
    public CheckStockResponse checkStock(@RequestPayload CheckStockRequest request) {
        BloodGroup group;
        try {
            group = BloodGroup.from(request.getBloodGroup());
        } catch (IllegalArgumentException e) {
            throw new SoapClientException(e.getMessage());
        }

        BloodStock stock = stockService.get(group);

        CheckStockResponse response = new CheckStockResponse();
        response.setBloodGroup(group.getLabel());
        response.setUnitsAvailable(stock.getUnitsAvailable());
        response.setLevel(stockService.levelFor(stock.getUnitsAvailable()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CheckEligibilityRequest")
    @ResponsePayload
    public CheckEligibilityResponse checkEligibility(@RequestPayload CheckEligibilityRequest request) {
        Donor donor;
        try {
            donor = donorService.getById(request.getDonorId());
        } catch (ResourceNotFoundException e) {
            throw new SoapClientException(e.getMessage());
        }

        Eligibility result = donorService.checkEligibility(donor);

        CheckEligibilityResponse response = new CheckEligibilityResponse();
        response.setDonorId(donor.getId());
        response.setDonorName(donor.getFullName());
        response.setEligible(result.eligible());
        response.setMessage(result.reason());
        if (result.nextEligibleDate() != null) {
            response.setNextEligibleDate(result.nextEligibleDate().toString());
        }
        return response;
    }
}
