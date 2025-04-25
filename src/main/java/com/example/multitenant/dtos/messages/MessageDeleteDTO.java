package com.example.multitenant.dtos.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// this is used to be sent to the topic NOT to be received from the client
@Getter
@Setter
@AllArgsConstructor
public class MessageDeleteDTO {
    private Integer id;
}
