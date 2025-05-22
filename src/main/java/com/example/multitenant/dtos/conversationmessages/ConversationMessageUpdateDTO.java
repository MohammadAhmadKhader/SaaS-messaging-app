package com.example.multitenant.dtos.conversationmessages;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ConversationMessageUpdateDTO {
    @NotNull(message = "content can not be empty")
    @Size(min = 1, message = "content can not be less than {min} characters")
    @Size(max = 256, message = "content can not be more than {max} characters")
    private String content;
}
