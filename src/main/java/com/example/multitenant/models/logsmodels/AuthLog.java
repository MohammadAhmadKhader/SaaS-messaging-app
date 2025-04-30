package com.example.multitenant.models.logsmodels;

import org.postgresql.translation.messages_bg;

import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;

@ToString
@DiscriminatorValue("AUTH")
@Getter
@Setter
@Entity
@Table(name = "auth_logs")
public class AuthLog extends BaseLog {
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user-agent", length = 512)
    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String getMessage() {
        var userName = this.getUser() == null ? "NOT FOUND" : this.getUser().getFullName();
        var userAgent = this.getUserAgent() == null ? "NOT FOUND" : this.getUserAgent();

        var message = "";
        if (this.getEventType().equals(LogEventType.LOGIN)) {
            message = String.format("user '%s' has logged in, user-agent: %s", userName, userAgent);

        } else if (this.getEventType().equals(LogEventType.REGISTER)) {
            message = String.format("user '%s' has registered, user-agent: %s", userName, userAgent);

        } else if (this.getEventType().equals(LogEventType.LOGOUT)) {
            message = String.format("user '%s' has logged out, user-agent: %s", userName, userAgent);

        } else {
            throw new UnknownException("invalid event type");
        }
        
        return message;
    }
}