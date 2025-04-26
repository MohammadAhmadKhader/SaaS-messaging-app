package com.example.multitenant.dtos.contents;

import java.time.Instant;
import java.time.LocalDateTime;

import com.example.multitenant.dtos.users.*;
import com.example.multitenant.models.Content;
import com.example.multitenant.models.enums.*;

public record ContentViewDTO( 
    Integer id,
    String title,
    String description,
    Status status,
    Integer organizationId,
    ContentType contentType,
    Instant createdAt,
    Instant updatedAt,
    UserWithoutPermissionsViewDTO user,
    String url
){
  
    public ContentViewDTO(Content content) {
        this(
            content.getId(),
            content.getTitle(),
            content.getDescription(),
            content.getStatus(),
            content.getOrganizationId(),
            content.getContentType(), 
            content.getCreatedAt(),
            content.getUpdatedAt(),
            content.getUser() == null ? null : content.getUser().toUserWithoutPermissionsViewDTO(),
            content.getUrl()
        );
    }
}
