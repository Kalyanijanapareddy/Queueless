
package com.queueless.controller;

import com.queueless.dto.BranchRequest;
import com.queueless.dto.BranchResponse;
import com.queueless.entity.Branch;
import com.queueless.entity.Organization;
import com.queueless.repository.BranchRepository;
import com.queueless.repository.OrganizationRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchRepository branchRepository;
    private final OrganizationRepository organizationRepository;

    public BranchController(BranchRepository branchRepository,
                            OrganizationRepository organizationRepository) {
        this.branchRepository = branchRepository;
        this.organizationRepository = organizationRepository;
    }

    private BranchResponse toResponse(Branch branch) {
        return new BranchResponse(
            branch.getId(),
            branch.getOrganization().getId(),
            branch.getName(),
            branch.getBranchCode(),
            branch.getAddressLine(),
            branch.getCity(),
            branch.getState(),
            branch.getPostalCode(),
            branch.getPhone(),
            branch.getActive()
        );
    }

    @GetMapping
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/organization/{organizationId}")
    public List<BranchResponse> getBranchesByOrganization(
            @PathVariable Long organizationId) {
        return branchRepository
                .findByOrganizationIdAndActiveTrue(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<BranchResponse> createBranch(
            @Valid @RequestBody BranchRequest request) {

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Organization not found"));

        Branch branch = new Branch();
        branch.setOrganization(organization);
        branch.setName(request.getName());
        branch.setBranchCode(request.getBranchCode());
        branch.setAddressLine(request.getAddressLine());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setPostalCode(request.getPostalCode());
        branch.setPhone(request.getPhone());
        branch.setActive(true);

        Branch saved = branchRepository.save(branch);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }
}
