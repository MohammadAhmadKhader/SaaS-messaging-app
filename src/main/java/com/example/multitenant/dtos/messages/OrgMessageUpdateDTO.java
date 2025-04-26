package com.example.multitenant.dtos.messages;

import com.example.multitenant.models.Message;
import com.example.multitenant.models.OrgMessage;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class OrgMessageUpdateDTO {
    @NotBlank
    @Size(min = 1, message = "content can not be less than {min} characters")
    @Size(max = 256, message = "content can not be more than {max} characters")
    private String content;

    public Message toModel() {
        var message = new OrgMessage();
        message.setContent(content);
        
        return message;
    }
}
