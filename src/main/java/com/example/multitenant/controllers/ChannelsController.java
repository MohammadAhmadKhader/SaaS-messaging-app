package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.channels.ChannelCreateDTO;
import com.example.multitenant.dtos.channels.ChannelOrderSwapDTO;
import com.example.multitenant.dtos.channels.ChannelUpdateDTO;
import com.example.multitenant.services.channels.ChannelsService;
import com.example.multitenant.utils.AppUtils;

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

    @GetMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CATEGORY_VIEW)" + " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Object> getChannelById(@PathVariable @ValidateNumberId Integer id, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        
        var channel = this.channelsService.findByIdAndOrganizationId(id, tenantId);
        if(channel == null) {
            var respBody = ApiResponses.GetNotFoundErr("channel", id);
            return ResponseEntity.badRequest().body(respBody);
        }
        
        var bodyResponse = ApiResponses.OneKey("channel", channel.toViewDTO());
        
        return ResponseEntity.ok(bodyResponse);
    }
    
    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CHANNEL_CREATE)" + " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Object> createChannel(@Valid @RequestBody ChannelCreateDTO dto, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        
        var channel = this.channelsService.create(dto.toModel(), tenantId, categoryId);
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
        
        var updatedChannel = this.channelsService.update(id, dto.toModel(), tenantId);
        if(updatedChannel == null) {
            var respBody = ApiResponses.GetNotFoundErr("channel", id);
            return ResponseEntity.badRequest().body(respBody);
        }
        
        var responseBody = ApiResponses.OneKey("channel", updatedChannel.toViewDTO());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }

    @PatchMapping("/swap-order")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CHANNEL_UPDATE)"+ " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Void> swapChannelOrder(@RequestBody ChannelOrderSwapDTO dto, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        this.channelsService.swapChannelOrder(dto, tenantId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CHANNEL_DELETE)"+ " and @customSPEL.hasCategoryAccess(#categoryId)")
    public ResponseEntity<Object> deleteChannel(@ValidateNumberId @PathVariable Integer id, @PathVariable @ValidateNumberId Integer categoryId) {
        var tenantId = AppUtils.getTenantId();
        this.channelsService.delete(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}
