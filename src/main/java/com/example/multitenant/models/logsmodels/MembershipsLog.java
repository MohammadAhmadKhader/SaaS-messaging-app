package com.example.multitenant.models.logsmodels;

import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.Organization;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@Setter
@DiscriminatorValue("MEMBERSHIP")
@NoArgsConstructor
@Entity
@Table(name = "memberships_logs")
public class MembershipsLog extends BaseOrganizationsLogs {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String getMessage() {
        String userName;

        userName = this.getUser() == null ? "NOT FOUND" : this.getUser().getFullName();

        var msg = "";
        if (this.getEventType().equals(LogEventType.JOIN)) {
            msg = String.format("user '%s' has joined the organization", userName);
        } else if(this.getEventType().equals(LogEventType.LEAVE)) {
            msg = String.format("user '%s' has left the organization", userName);
        } else {
            throw new UnknownException("invalid event type");
        }

        return msg;
    }
}