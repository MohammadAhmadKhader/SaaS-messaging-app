package com.example.multitenant.services.categories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.categories.CategoryOrderSwapDTO;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.Category;
import com.example.multitenant.models.Organization;
import com.example.multitenant.repository.CategoriesRepository;
import com.example.multitenant.services.security.OrganizationRolesService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final OrganizationRolesService organizationRolesService;
    
    public List<Category> findAllWithChannels(Integer organizationId) {
        return this.categoriesRepository.findAllByOrganizationIdWithChannels(organizationId);
    }

    public List<Category> findAllWithChannelsAndRoles(Integer organizationId) {
        return this.categoriesRepository.findAllByOrganizationIdWithChannelsAndRoles(organizationId);
    }
    
    public List<Category> findAllWithAuthorizedRoles(Integer organizationId) {
        return this.categoriesRepository.findAllByOrganizationIdWithAuthorizedRoles(organizationId);
    }

    public Category findByIdAndOrganizationId(Integer id, Integer organizationId) {
        return this.categoriesRepository.findByIdAndOrganizationId(id, organizationId);
    }
    public Category findByIdAndOrganizationIdWithAuthorizedRoles(Integer id, Integer organizationId) {
        return this.categoriesRepository.findByIdAndOrganizationIdWithAuthorizedRoles(id, organizationId);
    }
    
    public Category create(Category category, Integer organizationId) {
        category.setOrganizationId(organizationId);
        var latestOrderCategory = this.categoriesRepository.findLatestOrder(organizationId);
        
        Integer displayOrder;
        if(latestOrderCategory == null) {
            displayOrder = 1;
        } else {
            displayOrder = latestOrderCategory.getDisplayOrder() + 1;
        }

        category.setDisplayOrder(displayOrder);
        this.initializeRoles(category, organizationId);

        return this.categoriesRepository.saveAndFlush(category);
    }
    
    public Category update(Integer id, Category updatedCategory, Integer organizationId) {
        var existingCategory = this.categoriesRepository.findByIdAndOrganizationId(id, organizationId);
        if (existingCategory == null) {
            return null;
        }
        
        existingCategory.setName(updatedCategory.getName());
        
        return this.categoriesRepository.save(existingCategory);
    }
    
    public void delete(Integer id, Integer organizationId) {
        this.categoriesRepository.deleteByIdAndOrganizationId(id, organizationId);
    }

    
    @Transactional
    public void swapCategoryOrder(CategoryOrderSwapDTO dto, Integer orgId) {
        var cat1 = this.findOne(dto.getCategoryId1(), orgId);
        if(cat1 == null) {
            throw new ResourceNotFoundException("category", dto.getCategoryId1());
        }

        var cat2 = this.findOne(dto.getCategoryId2(), orgId);
        if (cat2 == null) {
            throw new ResourceNotFoundException("category", dto.getCategoryId2());
        }
        

        var tempOrder = cat1.getDisplayOrder();
        cat1.setDisplayOrder(cat2.getDisplayOrder());
        cat2.setDisplayOrder(tempOrder);

        categoriesRepository.saveAll(List.of(cat1, cat2));
    }

    public Category authorizeOrgRole(Integer id, Integer orgId, Integer roleId) {
        var cat = this.findOne(id, orgId);
        if (cat == null) {
            throw new ResourceNotFoundException("category", id);
        }

        var orgRole = this.organizationRolesService.findOne(roleId, orgId);
        if (orgRole == null) {
            throw new ResourceNotFoundException("organization role", roleId);
        }

        var isAdded = cat.getAuthorizedRoles().add(orgRole);
        if (!isAdded) {
            throw new UnknownException("an error has occured during attempt to add organization role to a category");
        }

        return this.categoriesRepository.save(cat);
    }

    public Category unAuthorizeOrgRole(Integer id, Integer orgId, Integer roleId) {
        var cat = this.findOne(id, orgId);
        if (cat == null) {
            throw new ResourceNotFoundException("category", id);
        }

        var orgRole = this.organizationRolesService.findOne(roleId, orgId);
        if (orgRole == null) {
            throw new ResourceNotFoundException("organization role", roleId);
        }

        var isRemoved = cat.getAuthorizedRoles().remove(orgRole);
        if (!isRemoved) {
            throw new UnknownException("an error has occured during attempt to remove organization role to a category");
        }

        return this.categoriesRepository.save(cat);
    }

    public Category findOne(Integer id, Integer orgId) {
        var probe = new Category();
        probe.setId(id);
        probe.setOrganizationId(orgId);

        return this.categoriesRepository.findOne(Example.of(probe)).orElse(null);
    }

    public long countOrganizationCategories(Integer orgId) {
        return this.categoriesRepository.countCategoriesByOrgId(orgId);
    }

    public void initializeRoles(Category category, Integer orgId) {
        var defRoles = this.organizationRolesService.findDefaultOrgRoles(orgId);
        category.setAuthorizedRoles(defRoles.stream().collect(Collectors.toSet()));
    }
}
