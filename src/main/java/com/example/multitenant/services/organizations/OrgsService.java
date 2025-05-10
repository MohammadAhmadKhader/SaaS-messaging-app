package com.example.multitenant.services.organizations;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.dtos.organizations.OrgsFilter;
import com.example.multitenant.dtos.shared.CursorPage;
import com.example.multitenant.dtos.users.UsersFilter;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.Membership;
import com.example.multitenant.models.Organization;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.FilesPath;
import com.example.multitenant.repository.OrgsRepository;
import com.example.multitenant.services.files.FilesService;
import com.example.multitenant.specificationsbuilders.OrgsSpecBuilder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrgsService {

    private final OrgsRepository organizationsRepository;
    private final OrgsCrudService organizationsCrudService;
    private final OrgsSpecService organizationsSpecService;
    private final FilesService filesService;

    public Page<Organization> findAllOrganization(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var result = this.organizationsRepository.findAll(pageable);

        return result;
    }

    public CursorPage<Organization, Long> search(OrgsFilter filter, Long cursor, int size) {
        var spec = OrgsSpecBuilder.build(filter);
        var result = this.organizationsSpecService.findAllWithCursor(spec, cursor, size, "id");

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

    public Organization deleteOrganizationById(Integer orgId) {
        var org = this.organizationsRepository.findById(orgId).orElse(null);
        if(org == null) {
            return null;
        }

        return org;
    }
    
    @Transactional
    public Organization findByIdThenDelete(Integer id) {
        var org = this.organizationsCrudService.findById(id);
        if(org == null) {
            return null;
        }

        this.organizationsRepository.delete(org);
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    filesService.deleteFile(FilesPath.ORGS_IMAGES, org.getImageUrl());
                }
            }
        );

        return org;
    }

    @Transactional
    public Organization findThenUpdate(Integer id, Organization org, MultipartFile image) {
        var updatedOrg = this.organizationsCrudService.findThenUpdate(id, (existingOrg) -> patcher(existingOrg, org));
        if(updatedOrg == null) {
            return null;
        }

        if(image != null) {
            var fileResponse = filesService.updateFile(image, FilesPath.ORGS_IMAGES, updatedOrg.getImageUrl());
            updatedOrg.setImageUrl(fileResponse.getUrl());
            return this.organizationsRepository.save(updatedOrg);
        }
        
        return updatedOrg;
    }

    public Organization findById(Integer id) {
        return this.organizationsCrudService.findById(id);
    }

    public Organization create(Organization org) {
        return this.organizationsCrudService.create(org);
    }

    public long countUserOrganizations(Long userId) {
        return this.organizationsRepository.countOrgsByUserId(userId);
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
