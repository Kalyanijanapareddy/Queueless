
package com.queueless.controller;

import com.queueless.dto.OrganizationRequest;
import com.queueless.dto.OrganizationResponse;
import com.queueless.entity.Organization;
import com.queueless.entity.ServiceCategory;
import com.queueless.repository.OrganizationRepository;
import com.queueless.repository.ServiceCategoryRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationRepository organizationRepository;
    private final ServiceCategoryRepository categoryRepository;

    public OrganizationController(
            OrganizationRepository organizationRepository,
            ServiceCategoryRepository categoryRepository) {
        this.organizationRepository = organizationRepository;
        this.categoryRepository = categoryRepository;
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getDescription(),
                organization.getRegistrationNumber(),
                organization.getEmail(),
                organization.getPhone(),
                organization.getWebsite(),
                organization.getActive(),
                organization.getCategory().getId()
        );
    }

    @GetMapping
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/category/{categoryId}")
    public List<OrganizationResponse> getOrganizationsByCategory(
            @PathVariable Long categoryId) {
        return organizationRepository
                .findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody OrganizationRequest request) {

        ServiceCategory category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"));

        Organization organization = new Organization();
        organization.setCategory(category);
        organization.setName(request.getName());
        organization.setDescription(request.getDescription());
        organization.setRegistrationNumber(
                request.getRegistrationNumber());
        organization.setEmail(request.getEmail());
        organization.setPhone(request.getPhone());
        organization.setWebsite(request.getWebsite());
        organization.setActive(true);

        Organization saved = organizationRepository.save(organization);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }
}


