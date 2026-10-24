
package com.queueless.controller;

import com.queueless.dto.ProfessionalRequest;
import com.queueless.dto.ProfessionalResponse;
import com.queueless.entity.Branch;
import com.queueless.entity.Professional;
import com.queueless.repository.BranchRepository;
import com.queueless.repository.ProfessionalRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {

    private final ProfessionalRepository professionalRepository;
    private final BranchRepository branchRepository;

    public ProfessionalController(
            ProfessionalRepository professionalRepository,
            BranchRepository branchRepository) {
        this.professionalRepository = professionalRepository;
        this.branchRepository = branchRepository;
    }

    private ProfessionalResponse toResponse(Professional professional) {
        return new ProfessionalResponse(
            professional.getId(),
            professional.getBranch().getId(),
            professional.getFullName(),
            professional.getDesignation(),
            professional.getQualification(),
            professional.getSpecialization(),
            professional.getEmail(),
            professional.getPhone(),
            professional.getActive()
        );
    }

    @GetMapping
    public List<ProfessionalResponse> getAllProfessionals() {
        return professionalRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/branch/{branchId}")
    public List<ProfessionalResponse> getByBranch(
            @PathVariable Long branchId) {
        return professionalRepository
                .findByBranchIdAndActiveTrue(branchId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ProfessionalResponse> createProfessional(
            @Valid @RequestBody ProfessionalRequest request) {

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Branch not found"));

        Professional professional = new Professional();
        professional.setBranch(branch);
        professional.setFullName(request.getFullName());
        professional.setDesignation(request.getDesignation());
        professional.setQualification(request.getQualification());
        professional.setSpecialization(request.getSpecialization());
        professional.setEmail(request.getEmail());
        professional.setPhone(request.getPhone());
        professional.setActive(true);

        Professional saved = professionalRepository.save(professional);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }
}
