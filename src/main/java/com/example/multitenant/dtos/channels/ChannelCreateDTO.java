package com.example.multitenant.dtos.channels;


import com.example.multitenant.models.Channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelCreateDTO {
    @NotBlank(message = "name is required")
    private String name;
    
    public Channel toModel() {
        var channel = new Channel();
        channel.setName(this.getName());
        
        return channel;
    }
}
