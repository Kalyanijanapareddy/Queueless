
package com.queueless.controller;

import com.queueless.entity.ServiceCategory;
import com.queueless.repository.ServiceCategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ServiceCategoryController {

    private final ServiceCategoryRepository categoryRepository;

    public ServiceCategoryController(
            ServiceCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<ServiceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
