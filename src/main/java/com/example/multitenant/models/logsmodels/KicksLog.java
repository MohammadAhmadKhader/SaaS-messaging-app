package com.example.multitenant.models.logsmodels;

import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@Setter
@DiscriminatorValue("KICK")
@NoArgsConstructor
@Entity
@Table(name = "kicks_logs")
public class KicksLog extends BaseOrganizationsLogs {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kicker_id")
    private User kicker;

    @Column(name = "kicked_id")
    private Long kickedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kicked_id", updatable = false, insertable = false)
    private User kicked;

    @Override
    public String getMessage() {
        var kickerName = this.getKicker() == null ? "NOT FOUND" : this.getKicker().getFullName();
        var kickedName = this.getKicked() == null ? "NOT FOUND" : this.getKicked().getFullName();

        var msg = "";
        if(this.getEventType().equals(LogEventType.KICK)) {
            msg = String.format("user %s was kicked from the organization by '%s'", kickedName, kickerName);
        } else {
            throw new UnknownException("invalid event type");
        }

        return msg;
    }
}