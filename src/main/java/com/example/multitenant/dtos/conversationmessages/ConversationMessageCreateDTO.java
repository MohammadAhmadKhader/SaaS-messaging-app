package com.example.multitenant.dtos.conversationmessages;

import com.example.multitenant.models.ConversationMessage;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class ConversationMessageCreateDTO {
    @NotNull(message = "content can not be empty")
    @Size(min = 1, message = "content can not be less than {min} characters")
    @Size(max = 256, message = "content can not be more than {max} characters")
    private String content;

    public ConversationMessage toModel() {
        var message = new ConversationMessage();
        message.setContent(content);

        return message;
    }
}