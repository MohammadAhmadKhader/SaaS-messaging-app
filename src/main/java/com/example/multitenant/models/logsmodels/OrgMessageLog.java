package com.example.multitenant.models.logsmodels;

import com.example.multitenant.models.OrgMessage;
import com.example.multitenant.models.User;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Setter
@DiscriminatorValue("ORG_MESSAGE")
@NoArgsConstructor
@Entity
@Table(name = "organization_messages_logs")
public class OrgMessageLog extends BaseOrganizationsLogs {
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private OrgMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String getMessage() {
        return null;
    }
}
