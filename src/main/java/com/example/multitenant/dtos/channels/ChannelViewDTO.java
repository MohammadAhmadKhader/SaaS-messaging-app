package com.example.multitenant.dtos.channels;

import java.time.Instant;

import com.example.multitenant.models.Channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelViewDTO {
    private Integer id;
    private String name;
    private Integer order;
    private Integer organizationId;
    private Integer categoryId;
    private Instant createdAt;
    private Instant updatedAt;

    public ChannelViewDTO(Channel channel) {
        setId(channel.getId());
        setName(channel.getName());
        setOrder(channel.getDisplayOrder());
        setOrganizationId(channel.getOrganizationId());
        setCategoryId(channel.getCategoryId());
        setCreatedAt(channel.getCreatedAt());
        setUpdatedAt(channel.getUpdatedAt());
    }
}
