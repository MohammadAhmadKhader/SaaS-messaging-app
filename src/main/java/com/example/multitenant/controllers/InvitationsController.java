package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.common.validators.contract.ValidateSize;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.invitations.InvitationAcceptDTO;
import com.example.multitenant.dtos.invitations.InvitationCancelRejectDTO;
import com.example.multitenant.dtos.invitations.InvitationSendDTO;
import com.example.multitenant.models.enums.InvitiationAction;
import com.example.multitenant.services.invitations.InvitationsService;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invitations")
public class InvitationsController {
    private final InvitationsService invitationsService;

    @GetMapping("")
    public ResponseEntity<Object> getYourInvitiations(Integer cursor, @ValidateSize Integer size) {
        var principal = SecurityUtils.getPrincipal();
        var userId = principal.getUser().getId();

        var cursorPage = this.invitationsService.getUserInvitationsWithCursor(userId, cursor, size);
        var body = cursorPage.toApiResponse("invitations", (invitations) -> {
            return invitations.stream().map((inv) -> inv.toViewDTO()).toList();
        });

        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/organizations")
    public ResponseEntity<Object> getOrganizationInvitations(Integer cursor, @ValidateSize Integer size) {
        var tenantId = AppUtils.getTenantId();

        var inviations = this.invitationsService.getOrganizationInvitationsWithCursor(tenantId, cursor, size);
        var res = ApiResponses.OneKey("invitations", inviations);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    public ResponseEntity<Object> sendInvitation(@Valid @RequestBody InvitationSendDTO dto) {
        var orgId = AppUtils.getTenantId();
        var principal = SecurityUtils.getPrincipal();
        var sender = principal.getUser();

        var inviation = this.invitationsService.sendInviteToUser(sender, orgId, dto.toModel());
        var res = ApiResponses.OneKey("invitation", inviation.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    
    @PatchMapping("/cancel-reject/{id}")
    public ResponseEntity<Object> cancelOrReject(
        @ValidateNumberId @PathVariable Integer id, 
        @Valid @RequestBody InvitationCancelRejectDTO dto) {
            
        var orgId = AppUtils.getTenantId();
        var principal = SecurityUtils.getPrincipal();
        var user = principal.getUser();
        
        var inv = this.invitationsService.findPendingInvitation(id, orgId);
        if(inv == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("pending invitation"));
        }

        var isCancelAction = dto.getAction() != InvitiationAction.CANCEL;
        var isRejectAction = dto.getAction() != InvitiationAction.REJECT;
        var reqBySender = inv.getSenderId() == user.getId();
        var reqByRecipient = inv.getRecipientId() == user.getId();

        if(!reqBySender && isCancelAction || !reqByRecipient && isRejectAction) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponses.Forbidden());
        }

        this.invitationsService.cancelOrRejectInvitation(inv, dto.getAction());

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<Object> acceptInvitiation(@Valid @RequestBody InvitationAcceptDTO dto) {
        var orgId = AppUtils.getTenantId();
        var principal = SecurityUtils.getPrincipal();
        var user = principal.getUser();
        
        var inv = this.invitationsService.findPendingInvitation(dto.getId(), orgId);
        if(inv == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("pending invitation"));
        }

        var reqBySender = inv.getRecipientId() == user.getId();
        if(!reqBySender) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponses.Forbidden());
        }

        this.invitationsService.acceptInvitationAndCreateMembership(inv);

        return ResponseEntity.accepted().build();
    }
}
