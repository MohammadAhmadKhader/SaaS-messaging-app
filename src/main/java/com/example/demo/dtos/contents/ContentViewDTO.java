package com.example.demo.dtos.contents;

import java.time.Instant;
import java.time.LocalDateTime;

import com.example.demo.dtos.users.UserViewDTO;
import com.example.demo.dtos.users.UserWithoutPermissionsViewDTO;
import com.example.demo.models.Content;
import com.example.demo.models.enums.ContentType;
import com.example.demo.models.enums.Status;

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
