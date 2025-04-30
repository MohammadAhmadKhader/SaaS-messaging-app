package com.example.multitenant.models.logsmodels;

import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.Channel;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@DiscriminatorValue("CHANNEL")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "channels_logs")
public class ChannelsLog extends BaseOrganizationsLogs {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Override
    public String getMessage() {
        var userName = this.getUser() == null ? "NOT FOUND" : this.getUser().getFirstName();
        var channelName = this.getChannel() == null ? "NOT FOUND" : this.getChannel().getName();

        var message = "";
        if(this.getEventType().equals(LogEventType.ORG_CHANNEL_CREATED)) {
            message = String.format("user '%s' has created a new channel with name '%s'", userName, channelName);

        } else if(this.getEventType().equals(LogEventType.ORG_CHANNEL_UPDATED)) {
            message = String.format("user '%s' has updated a channel with name '%s'", userName, channelName);

        } else if(this.getEventType().equals(LogEventType.ORG_CHANNEL_DELETED)) {
            message = String.format("user '%s' has deleted a channel with name '%s'", userName, channelName);

        } else {
            throw new UnknownException("invalid event type");
        }
        
        return message;
    }
}
