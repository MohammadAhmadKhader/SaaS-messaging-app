package com.example.demo.services.organizations;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.organizations.OrganizationViewDTO;
import com.example.demo.dtos.shared.FindAllResult;
import com.example.demo.models.Organization;
import com.example.demo.repository.organizations.OrganizationsRepository;
import com.example.demo.services.generic.GenericService;

@Service
public class OrganizationsService extends GenericService<Organization, Integer, OrganizationViewDTO> {

    private final OrganizationsRepository organizationsRepository;
    public OrganizationsService(OrganizationsRepository organizationsRepository) {
        super(organizationsRepository);
        this.organizationsRepository = organizationsRepository;
    }

    public FindAllResult<OrganizationViewDTO> findAllOrganization(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size);
        var result = this.organizationsRepository.findAll(pageable);
        var count = result.getTotalElements();

        var orgsView = result.getContent().stream().map((org) -> {
            return org.toViewDTO();
        }).toList();

        return new FindAllResult<>(orgsView, count, page, size);
    }
}
