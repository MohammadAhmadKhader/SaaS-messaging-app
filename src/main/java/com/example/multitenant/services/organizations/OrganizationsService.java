package com.example.multitenant.services.organizations;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.Membership;
import com.example.multitenant.models.Organization;
import com.example.multitenant.models.User;
import com.example.multitenant.repository.OrganizationsRepository;
import com.example.multitenant.services.generic.GenericService;

@Service
public class OrganizationsService extends GenericService<Organization, Integer> {

    private final OrganizationsRepository organizationsRepository;
    
    public OrganizationsService(OrganizationsRepository organizationsRepository) {
        super(organizationsRepository);
        this.organizationsRepository = organizationsRepository;
    }

    public Page<Organization> findAllOrganization(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var result = this.organizationsRepository.findAll(pageable);

        return result;
    }

    public Organization findOneWithOwner(Integer orgId) {
        return this.organizationsRepository.findByIdWithOwner(orgId);
    }

    public Organization setOwner(Organization org, User user) {
        org.setOwner(user);
        return this.organizationsRepository.save(org);
    }

    public boolean existsByName(String name) {
        var org = new Organization();
        org.setName(name);
        return this.organizationsRepository.exists(Example.of(org));
    }

    public Object deleteOrganizationById(Integer orgId) {
        var org = this.organizationsRepository.findById(orgId).orElse(null);
        if(org == null) {
            return null;
        }

        return null;
    }

    public Organization findThenUpdate(Integer id, Organization org) {
        return this.findThenUpdate(id, (existingOrg) -> patcher(existingOrg, org));
    }
    
    private void patcher(Organization target, Organization source) {
        var newIndustry = source.getIndustry();
        var newName = source.getName();

        if(newIndustry != null) {
            target.setIndustry(newIndustry);
        }

        if(newName != null) {
            target.setName(newName);
        }
    }
}
