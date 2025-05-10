package com.example.multitenant.controllers.dashboard;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.multitenant.common.resolvers.contract.*;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.restrictions.*;
import com.example.multitenant.services.cache.RestrictionsCacheSerivce;
import com.example.multitenant.services.restrictions.RestrictionsService;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/dashboard/restrictions")
public class AppDashboardRestrictionsController {
    private final RestrictionsService restrictionsService;
    private final RestrictionsCacheSerivce restrictionsCacheSerivce;

    @GetMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_RESTRICTION_VIEW)")
    public ResponseEntity<Object> getRestrictions(@HandlePage Integer page, @HandleSize Integer size, RestrictionsFilter fitler) {
            
        var restrictions = this.restrictionsService.getRestrictions(page, size, fitler);
        var count = restrictions.getTotalElements();
        var restrictionsViews = restrictions.map((rest) -> {
            return rest.toViewDTO();
        }).toList();
        
        var res = ApiResponses.GetAllResponse("restrictions", restrictionsViews, count, page, size);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_RESTRICTION_CREATE)")
    public ResponseEntity<Object> handleCreateRestriction(@Valid @RequestBody RestrictionCreateDTO dto) {
        var restriction = this.restrictionsService.restrictUser(dto.getUserId(), dto.toModel());
        this.restrictionsCacheSerivce.invalidateKey(dto.getUserId());

        var respBody = ApiResponses.OneKey("restriction", restriction.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_RESTRICTION_UPDATE)")
    public ResponseEntity<Object> handleUpdateRestriction(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody RestrictionUpdateDTO dto) {
        var restriction = this.restrictionsService.updateRestriction(id, dto.toModel());
        
        var now = Instant.now();
        var until = restriction.getUntil();
        var isRestRemoved = until.isBefore(now) || until.equals(now);
        if(isRestRemoved) {
            this.restrictionsCacheSerivce.invalidateKey(restriction.getUserId());
        }

        var respBody = ApiResponses.OneKey("restriction", restriction.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }
}