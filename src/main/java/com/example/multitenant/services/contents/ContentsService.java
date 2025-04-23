package com.example.multitenant.services.contents;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.Content;
import com.example.multitenant.repository.ContentsRepository;
import com.example.multitenant.services.cache.RedisService;
import com.example.multitenant.services.generic.GenericService;
import com.example.multitenant.specifications.ContentSpecifications;
import com.example.multitenant.utils.FiltersHandler;
import com.example.multitenant.utils.PageableHelper;

@Service
public class ContentsService extends GenericService<Content, Integer> {
    private final static String defaultSortBy = "createdAt";
    private final static String defaultSortDir = "DESC";

    private final ContentsRepository contentsRepository;
    private final ContentsServicesHelper servicesHelper;
    private final RedisService redisService;
    private final ContentsOwnershipService contentsOwnershipService;

    public ContentsService(ContentsRepository contentsRepository, RedisService redisService, ContentsOwnershipService contentsOwnershipService, ContentsServicesHelper servicesHelper) {
        super(contentsRepository);
        this.servicesHelper = servicesHelper;
        this.contentsRepository = contentsRepository;
        this.redisService = redisService;
        this.contentsOwnershipService = contentsOwnershipService;
    }

    public Content findByIdAndOrganizationId(Integer id, Integer organizationId) {
        return this.contentsRepository.findByIdAndOrganizationId(id, organizationId).orElse(null);
    }

    public Content createByUser(Content content, Integer tenantId) {
        return this.contentsOwnershipService.createOwn(content,tenantId);
    }

    public Content updateByUser(Integer id, Content content, Integer tenantId) {
        return this.contentsOwnershipService.updateOwn(id, content, tenantId);
    }

    public void deleteByUser(Integer id, Integer tenantId) {
        this.contentsOwnershipService.deleteOwn(id, tenantId);
    }

    public Page<Content> findAllPopulatedWithFilters(Integer page, Integer size, String sortDir, String sortBy, List<String> filters, Integer tenantId) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir,sortBy, sortDir, page, size);

        var parsedFilters = FiltersHandler.parseFilters(filters);
        var spec = applyFilters(parsedFilters, tenantId);
        
        return this.servicesHelper.findAllWithSpecifications(pageable, spec, null);
    }

    public Page<Content> findContentsByUserId(Integer page, Integer size, Long userId, Integer tenantId) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir, page, size);
        var result = this.contentsRepository.findContentsByUserId(userId, pageable);

        return result;
    }

    private Specification<Content> applyFilters(Map<String, String> filtersMap, Integer tenantId) {
        Specification<Content> spec = Specification.where(null);

        for(Map.Entry<String, String> entry: filtersMap.entrySet()){
            var field = entry.getKey();
            var val = entry.getValue();

            switch (field) {
                case "status":
                    spec = spec.and(ContentSpecifications.hasStatus(val));
                    break;
                case "contentType":
                    spec = spec.and(ContentSpecifications.hasContentType(val));
                    break;
                case "title":
                    spec = spec.and(ContentSpecifications.hasTitle(val));
                    break;
                case "createdAt":
                    spec = spec.and(ContentSpecifications.hasCreatedAt(val));
                    break;
            }
        }

        if(tenantId != null) {
            spec = spec.and(ContentSpecifications.hasOrganizationId(tenantId));
        }

        return spec;
    }
}
