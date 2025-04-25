package com.example.multitenant.dtos.channels;


import org.hibernate.annotations.ManyToAny;

import com.example.multitenant.models.Channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelCreateDTO {
    @NotBlank(message = "name is required")
    @Size(min = 2, message = "name can not be less than {min} characters")
    @Size(max = 32, message = "name can not be more than {max} characters")
    private String name;
    
    public Channel toModel() {
        var channel = new Channel();
        channel.setName(this.getName());
        
        return channel;
    }
}
