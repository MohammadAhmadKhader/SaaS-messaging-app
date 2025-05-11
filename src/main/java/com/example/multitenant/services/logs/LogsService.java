package com.example.multitenant.services.logs;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.models.*;
import com.example.multitenant.models.enums.LogEventType;
import com.example.multitenant.models.logsmodels.*;
import com.example.multitenant.repository.logsrepositories.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LogsService {
    private final BaseLogsRepository baseLogsRepository;
    private final KickLogsRepository kickLogsRepository;
    private final InvitationsLogsRepository invitationsLogsRepository;
    private final RolesAssignmentsRepository rolesAssignmentsRepository;
    private final MemberShipsLogsRepository memberShipsLogsRepository;
    private final CategoriesLogsRepository categoriesLogsRepository;
    private final ChannelsLogsRepository channelsLogsRepository;
    private final OrgMessagesLogsRepository orgMessagesLogsRepository;
    private final AuthLogsRepository authLogsRepository;

    public List<BaseLog> getAllOrgLogs() {
        return null;
    }

    public KicksLog createKickLog(User kicker, Long kickedId, Integer organizationId, LogEventType event) {
        var log = new KicksLog();
        if(!event.equals(LogEventType.KICK)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setOrganizationId(organizationId);
        log.setKickedId(kickedId);
        log.setKicker(kicker);
        log.setEventType(event);

        return this.kickLogsRepository.save(log);
    }

    public InvitationsLog createInvitationLog(User inviter, Long invitedId, Integer organizationId, LogEventType event) {
        var log = new InvitationsLog();
        if(!event.equals(LogEventType.INVITE_ACCEPTED) && !event.equals(LogEventType.INVITE_CANCELLED) 
        && !event.equals(LogEventType.INVITE_SENT)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setOrganizationId(organizationId);
        log.setInvitedId(invitedId);
        log.setInviter(inviter);
        log.setEventType(event);
        
        return this.invitationsLogsRepository.save(log);
    }

    public RolesAssignmentsLog createRolesAssignmentsLog(User assignedFrom, OrgRole role, 
    Long assignedToId ,Integer organizationId, LogEventType event) {

        var log = new RolesAssignmentsLog();
        if(!event.equals(LogEventType.ROLE_ASSIGN) && !event.equals(LogEventType.ROLE_UNASSIGN)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setOrganizationId(organizationId);
        log.setAssignedToId(assignedToId);
        log.setRole(role);
        log.setEventType(event);

        return this.rolesAssignmentsRepository.save(log);
    }

    public MembershipsLog createMembershipLog(User joiner, Integer orgId, LogEventType event) {
        var log = new MembershipsLog();
        if(!event.equals(LogEventType.JOIN) && !event.equals(LogEventType.LEAVE)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setOrganizationId(orgId);
        log.setUser(joiner);
        log.setEventType(event);

        return this.memberShipsLogsRepository.save(log);
    }

    public ChannelsLog createChannelsLog(User user, Channel channel, Integer orgId, LogEventType event) {
        var log = new ChannelsLog();
        if(!event.equals(LogEventType.ORG_CHANNEL_CREATED) && !event.equals(LogEventType.ORG_CHANNEL_UPDATED) 
        && !event.equals(LogEventType.ORG_CHANNEL_DELETED)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setOrganizationId(orgId);
        log.setUser(user);
        log.setChannel(channel);
        log.setEventType(event);

        return this.channelsLogsRepository.save(log);
    }

    public CategoriesLog createCategoriesLog(User user, Category category, Integer orgId, LogEventType event) {
        var log = new CategoriesLog();
        if(!event.equals(LogEventType.ORG_CATEGORY_CREATED) && !event.equals(LogEventType.ORG_CATEGORY_UPDATED)
         && !event.equals(LogEventType.ORG_CHANNEL_DELETED)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setOrganizationId(orgId);
        log.setUser(user);
        log.setCategory(category);
        log.setEventType(event);

        return this.categoriesLogsRepository.save(log);
    }

    public OrgMessageLog createOrgMessagesLog(User user, OrgMessage message, Integer orgId, LogEventType event) {
        var log = new OrgMessageLog();
        if(!event.equals(LogEventType.ORG_MESSAGE_DELETED) && !event.equals(LogEventType.ORG_MESSAGE_UPDATED)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setOrganizationId(orgId);
        log.setUser(user);
        log.setMessage(message);
        log.setEventType(event);

        return this.orgMessagesLogsRepository.save(log);
    }

    public AuthLog createAuthLogs(User user, String userAgent, String ipAddress, LogEventType event) {
        var log = new AuthLog();
        if(!event.equals(LogEventType.LOGIN) && !event.equals(LogEventType.REGISTER) 
        && !event.equals(LogEventType.LOGOUT) && !event.equals(LogEventType.RESET_PASSWORD)) {
            throw new InvalidOperationException("invalid event type");
        }

        log.setUser(user);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setEventType(event);

        return this.authLogsRepository.save(log);
    }
}