package com.example.multitenant.dtos.messages;

import com.example.multitenant.models.Message;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCreateDTO {
    @NotBlank(message = "content can not be empty")
    @Size(min = 1, message = "content can not be less than {min} characters")
    @Size(max = 256, message = "content can not be more than {max} characters")
    private String content;

    public Message toModel() {
        var message = new Message();
        message.setContent(content);

        return message;
    }
}