
package com.queueless.controller;

import com.queueless.dto.ServiceOfferingRequest;
import com.queueless.dto.ServiceOfferingResponse;
import com.queueless.entity.Branch;
import com.queueless.entity.Professional;
import com.queueless.entity.ServiceOffering;
import com.queueless.repository.BranchRepository;
import com.queueless.repository.ProfessionalRepository;
import com.queueless.repository.ServiceOfferingRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceOfferingController {

    private final ServiceOfferingRepository serviceRepository;
    private final BranchRepository branchRepository;
    private final ProfessionalRepository professionalRepository;

    public ServiceOfferingController(
            ServiceOfferingRepository serviceRepository,
            BranchRepository branchRepository,
            ProfessionalRepository professionalRepository) {
        this.serviceRepository = serviceRepository;
        this.branchRepository = branchRepository;
        this.professionalRepository = professionalRepository;
    }

    private ServiceOfferingResponse toResponse(ServiceOffering service) {
        var branch = service.getBranch();
        var organization = branch.getOrganization();
        var category = organization.getCategory();
        var professional = service.getProfessional();

        return new ServiceOfferingResponse(
                service.getId(),
                branch.getId(),
                professional == null ? null : professional.getId(),
                category.getId(),
                category.getName(),
                organization.getName(),
                branch.getName(),
                professional == null ? null : professional.getFullName(),
                professional == null ? null : professional.getSpecialization(),
                service.getName(),
                service.getDescription(),
                service.getEstimatedDurationMinutes(),
                service.getBaseFee(),
                service.getActive()
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/branch/{branchId}")
    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> getServicesByBranch(
            @PathVariable Long branchId) {
        return serviceRepository
                .findByBranchIdAndActiveTrue(branchId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/professional/{professionalId}")
    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> getServicesByProfessional(
            @PathVariable Long professionalId) {
        return serviceRepository
                .findByProfessionalIdAndActiveTrue(professionalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ServiceOfferingResponse> createService(
            @Valid @RequestBody ServiceOfferingRequest request) {

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Branch not found"));

        Professional professional = null;

        if (request.getProfessionalId() != null) {
            professional = professionalRepository
                    .findById(request.getProfessionalId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Professional not found"));

            if (!professional.getBranch().getId().equals(branch.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Professional must belong to the selected branch");
            }
        }

        ServiceOffering service = new ServiceOffering();
        service.setBranch(branch);
        service.setProfessional(professional);
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setEstimatedDurationMinutes(
                request.getEstimatedDurationMinutes());
        service.setBaseFee(request.getBaseFee());
        service.setActive(true);

        ServiceOffering saved = serviceRepository.save(service);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }
}

