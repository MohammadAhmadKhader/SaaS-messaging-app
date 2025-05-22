package com.example.multitenant.dtos.messages;

import com.example.multitenant.models.OrgMessage;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class OrgMessageUpdateDTO {
    @NotNull(message = "content can not be empty")
    @Size(min = 1, message = "content can not be less than {min} characters")
    @Size(max = 256, message = "content can not be more than {max} characters")
    private String content;

    public OrgMessage toModel() {
        var message = new OrgMessage();
        message.setContent(content);
        
        return message;
    }
}
