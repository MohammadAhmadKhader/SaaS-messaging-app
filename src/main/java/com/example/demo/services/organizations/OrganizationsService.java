package com.example.demo.services.organizations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.models.Organization;
import com.example.demo.repository.OrganizationsRepository;
import com.example.demo.services.generic.GenericService;

@Service
public class OrganizationsService extends GenericService<Organization, Integer> {

    private final OrganizationsRepository organizationsRepository;
    public OrganizationsService(OrganizationsRepository organizationsRepository) {
        super(organizationsRepository);
        this.organizationsRepository = organizationsRepository;
    }

    public Page<Organization> findAllOrganization(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size);
        var result = this.organizationsRepository.findAll(pageable);

        return result;
    }
}
