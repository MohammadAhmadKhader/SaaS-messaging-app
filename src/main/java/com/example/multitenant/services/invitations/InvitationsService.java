package com.example.multitenant.services.invitations;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.shared.CursorPage;
import com.example.multitenant.exceptions.*;
import com.example.multitenant.models.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.repository.InvitationsRepository;
import com.example.multitenant.services.cache.OrgRestrictionsCacheSerivce;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.users.UsersService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvitationsService {

    private final InvitationsRepository invitationsRepository;
    private final UsersService usersService;
    private final MemberShipService memberShipService;
    private final OrgRestrictionsCacheSerivce orgRestrictionsCacheSerivce;

    public CursorPage<Invitation, Integer> getUserInvitationsWithCursor(Long userId, Integer cursor ,Integer size) {
        var pageable = PageRequest.of(0, size + 1, Sort.by("createdAt","id").descending());

        List<Invitation> invitations;
        if(cursor == null) {
            invitations = this.invitationsRepository.findByRecipientId(userId, pageable);
        } else {
            invitations = this.invitationsRepository.findByRecipientIdAndCursor(userId, cursor, pageable);
        }

        var hasNext = invitations.size() > size;

        Integer nextCursor;
        if(hasNext) {
            nextCursor = invitations.get(size - 1).getId();
            invitations = invitations.subList(0, size); 
        } else {
            nextCursor = null;
        }
        
        return CursorPage.of(invitations, nextCursor, hasNext);
    }

    public CursorPage<Invitation, Integer> getOrganizationInvitationsWithCursor(Integer organizationId, Integer cursor ,Integer size) {
        var pageable = PageRequest.of(0, size + 1, Sort.by("createdAt", "id").descending());

        List<Invitation> invitations;
        if(cursor == null) {
            invitations = this.invitationsRepository.findByOrganizationId(organizationId, pageable);
        } else {
            invitations = this.invitationsRepository.findByOrganizationIdAndCursor(organizationId, cursor, pageable);
        }
        
        var hasNext = invitations.size() > size;
        var nextCusror = hasNext ? invitations.get(invitations.size() - 1).getId(): null;
        if(hasNext) {
            invitations = invitations.subList(0, size);
        }
        
        return CursorPage.of(invitations, nextCusror, hasNext);
    }

    public Invitation sendInviteToUser(User sender, Integer orgId, Invitation invitation) {
        var recipientId = invitation.getRecipientId();
        var isMember = this.memberShipService.isMember(invitation.getOrganizationId(), recipientId);
        if(isMember) {
            throw new InvalidOperationException("user already a member");
        }

        var existingInvitation = this.findPendingInvitation(recipientId, orgId);
        if(existingInvitation != null) {
            throw new InvalidOperationException("user already has an invitation");
        }

        var user = this.usersService.findById(recipientId);
        if(user == null) {
            throw new ResourceNotFoundException("user", recipientId);
        }

        var org = new Organization();
        org.setId(orgId);

        invitation.setRecipient(user);
        invitation.setSender(sender);
        invitation.setOrganization(org);
        
        return this.invitationsRepository.save(invitation);
    }

    public Invitation findThenCancelOrRejectInvitation(Integer invId, Integer orgId ,InvitiationAction newStatus) {
        var existingInvitation = this.findPendingInvitation(invId, orgId);
        if(existingInvitation == null) {
            throw new ResourceNotFoundException("invitation");
        }

        return cancelOrRejectInvitation(existingInvitation, newStatus);
    }

    public Invitation cancelOrRejectInvitation(Invitation inv ,InvitiationAction action) {
        InvitationStatus newStatus;
        if(InvitiationAction.CANCEL == action) {
            newStatus = InvitationStatus.CANCELLED;
        } else if (InvitiationAction.REJECT == action) {
            newStatus = InvitationStatus.REJECTED;
        } else {
            throw new InvalidOperationException(String.format("invalid action recieved %s", action.toString()));
        }

        if(inv.getStatus().equals(newStatus)) {
            throw new InvalidOperationException("can't re-set the invitation to same status");
        }
        
        inv.setStatus(newStatus);
        return this.invitationsRepository.save(inv);
    }

    public Invitation acceptInvitation(Invitation inv) {
        inv.setStatus(InvitationStatus.ACCEPTED);
        return this.invitationsRepository.save(inv);
    }

    @Transactional
    public Membership acceptInvitationAndCreateMembership(Invitation inv) {
        var acceptedInv = this.acceptInvitation(inv);
        var isRestricted = this.orgRestrictionsCacheSerivce.getIsRestricted(inv.getOrganizationId(), inv.getRecipientId());
        var membership = this.memberShipService.joinOrganization(acceptedInv.getOrganizationId(), acceptedInv.getRecipientId(), isRestricted);
        
        return membership;
    }

    public Invitation findPendingInvitation(Long recipientId, Integer orgId) {
        var probe = new Invitation();
        probe.setRecipientId(recipientId);
        probe.setOrganizationId(orgId);
        probe.setStatus(InvitationStatus.PENDING);

        return this.invitationsRepository.findOne(Example.of(probe)).orElse(null);
    }

    public Invitation findPendingInvitation(Integer invId, Integer orgId) {
        var probe = new Invitation();
        probe.setId(invId);
        probe.setOrganizationId(orgId);
        probe.setStatus(InvitationStatus.PENDING);

        return this.invitationsRepository.findOne(Example.of(probe)).orElse(null);
    }
}
