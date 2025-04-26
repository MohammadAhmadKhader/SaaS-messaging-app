package com.example.multitenant.dtos.channels;
import java.time.Instant;
import java.util.List;

import com.example.multitenant.dtos.messages.*;
import com.example.multitenant.models.Channel;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelWithOrgMessagesViewDTO {
    private Integer id;
    private String name;
    private Integer order;
    private Integer organizationId;
    private Integer categoryId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrgMessageWithUserViewDTO> messages;

    public ChannelWithOrgMessagesViewDTO(Channel channel) {
        setId(channel.getId());
        setName(channel.getName());
        setOrder(channel.getDisplayOrder());
        setOrganizationId(channel.getOrganizationId());
        setCategoryId(channel.getCategoryId());
        setCreatedAt(channel.getCreatedAt());
        setUpdatedAt(channel.getUpdatedAt());
        setMessages(channel.getMessages().stream().map((msg) -> msg.toViewDTOWithUser()).toList());
    }
}
