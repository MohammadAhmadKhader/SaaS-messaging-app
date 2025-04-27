package com.example.multitenant.dtos.friendrequests;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter 
public class FriendRequestSendDTO {
    @NotNull(message = "receiver id can not be null")
    @Min(value = 1 ,message = "receiver id can not be less than {value}")
    private Long receiverId;
}
