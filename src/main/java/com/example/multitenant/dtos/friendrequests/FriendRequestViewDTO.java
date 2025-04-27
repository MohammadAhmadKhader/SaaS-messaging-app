package com.example.multitenant.dtos.friendrequests;

import java.time.Instant;

import com.example.multitenant.models.FriendRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestViewDTO {
    private Integer id;
    private Long senderId;
    private String firstName;
    private String lastName;
    private Instant createdAt;

    public static FriendRequestViewDTO fromEntity(FriendRequest request) {
        return FriendRequestViewDTO.builder()
                .id(request.getId())
                .senderId(request.getSender().getId())
                .firstName(request.getSender().getFirstName())
                .lastName(request.getSender().getLastName())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
