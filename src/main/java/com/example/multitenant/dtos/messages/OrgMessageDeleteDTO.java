package com.example.multitenant.dtos.messages;

import lombok.*;

// this is used to be sent to the topic NOT to be received from the client
@Getter
@Setter
@AllArgsConstructor
public class OrgMessageDeleteDTO {
    private Integer id;
}
