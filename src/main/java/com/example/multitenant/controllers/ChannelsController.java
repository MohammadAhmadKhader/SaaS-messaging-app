package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.channels.*;
import com.example.multitenant.models.enums.LogEventType;
import com.example.multitenant.services.channels.ChannelsService;
import com.example.multitenant.services.logs.LogsService;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories/{categoryId}/channels")
public class ChannelsController {
    private final ChannelsService channelsService;
    private final LogsService logsService;

    @GetMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CATEGORY_VIEW)" + " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Object> getChannelById(@PathVariable @ValidateNumberId Integer id, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        
        // null case handled inside
        var channel = this.channelsService.findWithMessagesByIdAndOrganizationId(id, tenantId, categoryId);
        var bodyResponse = ApiResponses.OneKey("channel", channel.toViewDTOWithMessages());
        
        return ResponseEntity.ok(bodyResponse);
    }
    
    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CHANNEL_CREATE)" + " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Object> createChannel(@Valid @RequestBody ChannelCreateDTO dto, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();
        
        var channel = this.channelsService.create(dto.toModel(), tenantId, categoryId);
        this.logsService.createChannelsLog(user, channel, tenantId, LogEventType.ORG_CHANNEL_CREATED);

        var channelView = channel.toViewDTO();
        var responseBody = ApiResponses.OneKey("channel", channelView);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CHANNEL_UPDATE)"+ " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Object> updateChannel(
        @ValidateNumberId @PathVariable Integer id, 
        @Valid @RequestBody ChannelUpdateDTO dto,
        @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();
        
        var updatedChannel = this.channelsService.update(id, dto.toModel(), tenantId, categoryId);
        if(updatedChannel == null) {
            var respBody = ApiResponses.GetNotFoundErr("channel", id);
            return ResponseEntity.badRequest().body(respBody);
        }
        this.logsService.createChannelsLog(user, updatedChannel, tenantId, LogEventType.ORG_CATEGORY_UPDATED);
        
        var responseBody = ApiResponses.OneKey("channel", updatedChannel.toViewDTO());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }

    @PatchMapping("/swap-order")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CHANNEL_UPDATE)"+ " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Void> swapChannelOrder(@RequestBody ChannelOrderSwapDTO dto, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        this.channelsService.swapChannelOrder(dto, tenantId, categoryId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CHANNEL_DELETE)"+ " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Object> deleteChannel(@ValidateNumberId @PathVariable Integer id, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        this.channelsService.delete(id, tenantId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
