package com.example.demo.services.contents;

import java.text.Collator;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.method.P;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.models.Content;
import com.example.demo.repository.contents.ContentsRepository;
import com.example.demo.services.cache.RedisService;
import com.example.demo.services.generic.GenericService;
import com.example.demo.services.ownership.ContentsOwnershipService;
import com.example.demo.dtos.contents.ContentViewDTO;
import com.example.demo.dtos.shared.FindAllResult;
import com.example.demo.specifications.ContentSpecifications;
import com.example.demo.utils.FiltersHandler;
import com.example.demo.utils.PageableHelper;

@Service
public class ContentsService extends GenericService<Content, Integer, ContentViewDTO> {
    private final static String defaultSortBy = "createdAt";
    private final static String defaultSortDir = "DESC";

    private final ContentsRepository contentsRepository;
    private final RedisService redisService;
    private final ContentsOwnershipService contentsOwnershipService;

    public ContentsService(ContentsRepository contentsRepository, RedisService redisService, ContentsOwnershipService contentsOwnershipService) {
        super(contentsRepository);
        this.contentsRepository = contentsRepository;
        this.redisService = redisService;
        this.contentsOwnershipService = contentsOwnershipService;
    }

    public Content createByUser(Content content) {
        return contentsOwnershipService.createOwn(content);
    }

    public Content updateByUser(Integer id, Content content) {
        return contentsOwnershipService.updateOwn(id, content);
    }

    public void deleteByUser(Integer id) {
        contentsOwnershipService.deleteOwn(id);
    }

    public FindAllResult<ContentViewDTO> findAllPopulatedWithFilters(Integer page, Integer size, String sortDir, String sortBy, List<String> filters) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir,sortBy, sortDir, page, size);

        var parsedFilters = FiltersHandler.parseFilters(filters);
        var spec = applyFilters(parsedFilters);
        
        var result = contentsRepository.findAllWithSpecifications(spec ,pageable);
        var count = result.getTotalElements();

        var contentsViews = result.getContent().stream().map((content) -> {
            return content.toViewDTO();
        }).toList();

        return new FindAllResult<>(contentsViews, count, page, size);
    }

    public FindAllResult<ContentViewDTO> findContentsByUserId(Integer page, Integer size, Long userId) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir, page, size);
        
        var result = contentsRepository.findContentsByUserId(userId, pageable);
        var count = result.getTotalElements();

        var contentsViews = result.getContent().stream().map((content) -> {
            return content.toViewDTO();
        }).toList();

        return new FindAllResult<>(contentsViews, count, page, size);
    }

    private Specification<Content> applyFilters(Map<String, String> filtersMap) {
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

        return spec;
    }
}
