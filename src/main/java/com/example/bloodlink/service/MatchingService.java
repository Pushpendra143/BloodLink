package com.example.bloodlink.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.bloodlink.dto.DonorMatch;
import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.BloodRequest;
import com.example.bloodlink.model.Donor;
import com.example.bloodlink.repository.DonorRepository;

@Service
public class MatchingService {

    private final DonorRepository donorRepository;
    private final DonorService donorService;
    private final BloodRequestService requestService;

    public MatchingService(DonorRepository donorRepository,
                           DonorService donorService,
                           BloodRequestService requestService) {
        this.donorRepository = donorRepository;
        this.donorService = donorService;
        this.requestService = requestService;
    }

    /*
     * Finds donors for a request:
     *   1. same city as the hospital
     *   2. blood group compatible with the patient
     *   3. eligible today (age, 90 day gap, active)
     * Exact blood group matches are listed first, then the donors who have
     * waited the longest since their last donation (or never donated).
     */
    public List<DonorMatch> findMatches(Long requestId) {
        BloodRequest request = requestService.getById(requestId);
        requestService.ensureOpen(request);

        BloodGroup target = request.getBloodGroup();
        Set<BloodGroup> acceptable = target.compatibleDonors();
        String city = request.getHospital().getCity();

        Comparator<Donor> order = Comparator
                .comparing((Donor d) -> d.getBloodGroup() != target)
                .thenComparing(Donor::getLastDonationDate, Comparator.nullsFirst(Comparator.naturalOrder()));

        return donorRepository.findByCityIgnoreCaseAndBloodGroupInAndActiveTrue(city, acceptable).stream()
                .filter(d -> donorService.checkEligibility(d).eligible())
                .sorted(order)
                .map(d -> new DonorMatch(d.getId(), d.getFullName(), d.getBloodGroup(),
                        d.getPhone(), d.getBloodGroup() == target))
                .toList();
    }
}
