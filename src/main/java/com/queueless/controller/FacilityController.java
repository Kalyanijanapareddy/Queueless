
package com.queueless.controller;

import com.queueless.dto.FacilityRequest;
import com.queueless.dto.FacilityResponse;
import com.queueless.entity.Branch;
import com.queueless.entity.Facility;
import com.queueless.repository.BranchRepository;
import com.queueless.repository.FacilityRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
public class FacilityController {

    private final FacilityRepository facilityRepository;
    private final BranchRepository branchRepository;

    public FacilityController(FacilityRepository facilityRepository,
                              BranchRepository branchRepository) {
        this.facilityRepository = facilityRepository;
        this.branchRepository = branchRepository;
    }

    private FacilityResponse toResponse(Facility facility) {
        return new FacilityResponse(
            facility.getId(),
            facility.getBranch().getId(),
            facility.getName(),
            facility.getDescription(),
            facility.getActive()
        );
    }

    @GetMapping
    public List<FacilityResponse> getAllFacilities() {
        return facilityRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/branch/{branchId}")
    public List<FacilityResponse> getFacilitiesByBranch(
            @PathVariable Long branchId) {
        return facilityRepository
                .findByBranchIdAndActiveTrue(branchId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<FacilityResponse> createFacility(
            @Valid @RequestBody FacilityRequest request) {

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Branch not found"));

        Facility facility = new Facility();
        facility.setBranch(branch);
        facility.setName(request.getName());
        facility.setDescription(request.getDescription());
        facility.setActive(true);

        Facility saved = facilityRepository.save(facility);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }
}
