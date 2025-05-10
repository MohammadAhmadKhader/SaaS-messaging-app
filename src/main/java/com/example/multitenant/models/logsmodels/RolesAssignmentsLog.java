package com.example.multitenant.models.logsmodels;

import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.OrgRole;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@Setter
@DiscriminatorValue("ROLE_ASSIGNMENT")
@NoArgsConstructor
@Entity
@Table(name = "roles_assignments_logs")
public class RolesAssignmentsLog extends BaseOrganizationsLogs {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_from_id")
    private User assignedFrom;

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", insertable = false, updatable = false)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private OrgRole role;

    @Override
    public String getMessage() {
        String fromName;
        String roleName;
        String toName;

        toName = this.getAssignedTo() == null ? "NOT FOUND" : this.getAssignedTo().getFullName();
        fromName = this.getAssignedFrom() == null ? "NOT FOUND" : this.getAssignedFrom().getFullName();
        roleName = this.getRole() == null ? "NOT FOUND" : this.getRole().getDisplayName();

        var msg = "";
        if (this.getEventType().equals(LogEventType.ROLE_ASSIGN)) {
            msg = String.format("user '%s' has assigned role '%s' to user '%s'", fromName, roleName, toName);
        } else if(this.getEventType().equals(LogEventType.ROLE_UNASSIGN)) {
            msg = String.format("user '%s' has un-assigned role '%s' to user '%s'", fromName, roleName, toName);
        } else {
            throw new UnknownException("invalid event type");
        }

        return msg;
    }
}