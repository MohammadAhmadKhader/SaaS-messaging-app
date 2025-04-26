package com.example.multitenant.dtos.channels;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.models.Channel;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelUpdateDTO {
    @NotBlank(message = "name can not be empty")
    @Size(min = 2, message = "name can not be less than {min} characters")
    @Size(max = 32, message = "name can not be more than {max} characters")
    private String name;
    
    public Channel toModel() {
        var channel = new Channel();
        channel.setName(this.getName());
        
        return channel;
    }
}
