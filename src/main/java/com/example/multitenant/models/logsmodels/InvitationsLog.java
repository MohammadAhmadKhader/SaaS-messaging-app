package com.example.multitenant.models.logsmodels;

import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@DiscriminatorValue("INVITE")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "invitations_logs")
public class InvitationsLog extends BaseOrganizationsLogs {
    @Column(name = "inviter_id", updatable = false, insertable = false)
    private Long inviterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;

    @Column(name = "invited_id")
    private Long invitedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_id", updatable = false, insertable = false)
    private User invited;

    @Override
    public String getMessage() {
        var inviterName = this.getInviter() == null ? "NOT FOUND" : this.getInviter().getFullName();
        var invitedName = this.getInvited() == null ? "NOT FOUND" : this.getInvited().getFullName();

        var msg = "";
        if(this.getEventType().equals(LogEventType.INVITE_SENT)) {
            msg = String.format("user '%s' was invited by user '%s'", invitedName, inviterName);
        } else if(this.getEventType().equals(LogEventType.INVITE_ACCEPTED)) {
            msg = String.format("user '%s' has accepted invitation was sent by '%s'", invitedName, inviterName);
        } else if(this.getEventType().equals(LogEventType.INVITE_CANCELLED)) {
            msg = String.format("user '%s' has cancelled invitation was sent by user '%s'", invitedName, inviterName);
        } else {
            throw new UnknownException("invalid event type");
        }

        return msg;
    }
}

